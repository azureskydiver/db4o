/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.*;
import com.db4o.reflect.*;

final class YapClassCollection extends YapMeta implements UseSystemTransaction {

    private YapClass i_addingMembersTo;

    private Collection4 i_classes;
    private Hashtable4 i_creating;
    
    private final YapStream i_stream;
    private final Transaction i_systemTrans;

    private Hashtable4 i_yapClassByBytes;
    private Hashtable4 i_yapClassByClass;
    private Hashtable4 i_yapClassByID;
    
    private int i_yapClassCreationDepth;
    private Queue4 i_initYapClassesOnUp;


    YapClassCollection(Transaction a_trans) {
        i_systemTrans = a_trans;
        i_stream = a_trans.i_stream;
        i_initYapClassesOnUp = new Queue4();
    }

    void addYapClass(YapClass yapClass) {
        i_stream.setDirty(this);
        i_classes.add(yapClass);
        if(yapClass.stateUnread()){
            i_yapClassByBytes.put(yapClass.i_nameBytes, yapClass);
        }else{
            i_yapClassByClass.put(yapClass.classReflector(), yapClass);
        }
        if (yapClass.getID() == 0) {
            yapClass.write(i_stream, i_systemTrans);
        }
        i_yapClassByID.put(yapClass.getID(), yapClass);
    }

    void checkChanges() {
        Iterator4 i = i_classes.iterator();
        while (i.hasNext()) {
            ((YapClass)i.next()).checkChanges();
        }
    }

    /**
	 * We always work from parent to child. If the Child is a member on the
	 * parent, we have a circular dependancy problem. This method takes care.
	 */
    private void classAddMembers(YapClass yapClass) {
        
        if (i_addingMembersTo != null) {
            i_addingMembersTo.addMembersAddDependancy(yapClass);
            return;
        }

        YapClass ancestor = yapClass.getAncestor();
        if (ancestor != null) {
            classAddMembers(ancestor);
        }
        i_addingMembersTo = yapClass;
        yapClass.addMembers(i_stream);
        
        yapClass.storeStaticFieldValues(i_systemTrans, true);
        
        i_addingMembersTo = null;
        YapClass[] dependancies = yapClass.getMembersDependancies();
        for (int i = 0; i < dependancies.length; i++) {
            classAddMembers(dependancies[i]);
        }
        yapClass.write(i_stream, i_stream.getSystemTransaction());

        // the dependancies need a rewrite
        // since our own ID only is available after
        // write

        for (int i = 0; i < dependancies.length; i++) {
            dependancies[i].setStateDirty();
            dependancies[i].write(i_stream, i_stream.getSystemTransaction());
        }
    }
    
    final boolean createYapClass(YapClass a_yapClass, IClass a_class) {
        i_yapClassCreationDepth++;
        IClass superClass = a_class.getSuperclass();
        YapClass superYapClass = null;
        if (superClass != null && superClass != i_stream.i_handlers.ICLASS_OBJECT) {
            superYapClass = getYapClass(superClass, true);
        }
        boolean ret = i_stream.createYapClass(a_yapClass, a_class, superYapClass);
        i_yapClassCreationDepth--;
        initYapClassesOnUp();
        return ret;
    }


    boolean fieldExists(String a_field) {
        YapClassCollectionIterator i = iterator();
        while (i.hasNext()) {
            if (i.nextClass().getYapField(a_field) != null) {
                return true;
            }
        }
        return false;
    }

    Collection4 forInterface(Class clazz) {
        Collection4 col = new Collection4();
        YapClassCollectionIterator i = iterator();
        while (i.hasNext()) {
            YapClass yc = i.nextClass();
            if (clazz.isAssignableFrom(yc.getJavaClass())) {
                boolean found = false;
                Iterator4 j = col.iterator();
                while (j.hasNext()) {
                    YapClass existing = (YapClass)j.next();
                    YapClass higher = yc.getHigherHierarchy(existing);
                    if (higher != null) {
                        found = true;
                        if (higher == yc) {
                            col.remove(existing);
                            col.add(yc);
                        }
                        break;
                    }
                }
                if (!found) {
                    col.add(yc);
                }
            }
        }
        return col;
    }

    byte getIdentifier() {
        return YapConst.YAPCLASSCOLLECTION;
    }

    YapClass getYapClass(IClass a_class, boolean a_create) {
        YapClass yapClass = (YapClass)i_yapClassByClass.get(a_class);
        if (yapClass == null) {
            byte[] bytes = i_stream.i_stringIo.write(a_class.getName());
            yapClass = (YapClass)i_yapClassByBytes.remove(bytes);
            readYapClass(yapClass, a_class);
        }
        

        if (yapClass != null || (!a_create)) {
            return yapClass;
        }
        
        yapClass = (YapClass)i_creating.get(a_class);
        
        if(yapClass != null){
            return yapClass;
        }
        
        yapClass = new YapClass();
        
        i_creating.put(a_class, yapClass);
        
        if(! createYapClass(yapClass, a_class)){
            i_creating.remove(a_class);
            return null;
        }

        // YapStream#createYapClass may add the YapClass already,
        // so we have to check again
        
        boolean addMembers = false;
        
        if (i_yapClassByClass.get(a_class) == null) {
            addYapClass(yapClass);
            addMembers = true;
        }
        
        int id = yapClass.getID();
        if(id == 0){
            yapClass.write(i_stream, i_stream.getSystemTransaction());
            id = yapClass.getID();
        }
        
        if(i_yapClassByID.get(id) == null){
            i_yapClassByID.put(id, yapClass);
            addMembers = true;
        }
        
        if(addMembers || yapClass.i_fields == null){
            classAddMembers(yapClass);
        }
        
        i_creating.remove(a_class);

        return yapClass;
    }

    YapClass getYapClass(int a_id) {
        return readYapClass((YapClass)i_yapClassByID.get(a_id), null);
    }

    YapClass getYapClass(String a_name) {
        byte[] bytes = i_stream.i_stringIo.write(a_name);
        YapClass yapClass = (YapClass)i_yapClassByBytes.remove(bytes);
        readYapClass(yapClass, null);
        if (yapClass == null) {
            YapClassCollectionIterator i = iterator();
            while (i.hasNext()) {
                yapClass = i.nextClass();
                if (a_name.equals(yapClass.getName())) {
                    return yapClass;
                }
            }
            return null;
        }
        return yapClass;
    }

    void initOnUp(Transaction systemTrans) {
        i_yapClassCreationDepth++;
        systemTrans.i_stream.showInternalClasses(true);
        Iterator4 i = i_classes.iterator();
        while (i.hasNext()) {
            ((YapClass)i.next()).initOnUp(systemTrans);
        }
        systemTrans.i_stream.showInternalClasses(false);
        i_yapClassCreationDepth--;
        initYapClassesOnUp();
    }

    void initTables(int a_size) {
        i_classes = new Collection4();
        i_yapClassByBytes = new Hashtable4(a_size);
        if (a_size < 16) {
            a_size = 16;
        }
        i_yapClassByClass = new Hashtable4(a_size);
        i_yapClassByID = new Hashtable4(a_size);
        i_creating = new Hashtable4(1);
    }
    
    private void initYapClassesOnUp() {
        if(i_yapClassCreationDepth == 0){
            YapClass yc = (YapClass)i_initYapClassesOnUp.next();
            while(yc != null){
                yc.initOnUp(i_systemTrans);
                yc = (YapClass)i_initYapClassesOnUp.next();
            }
        }
    }
    
    YapClassCollectionIterator iterator(){
        return new YapClassCollectionIterator(this, i_classes.i_first);
    }

    int ownLength() {
        return YapConst.OBJECT_LENGTH
            + YapConst.YAPINT_LENGTH
            + (i_classes.size() * YapConst.YAPID_LENGTH);
    }

    void purge() {
        Iterator4 i = i_classes.iterator();
        while (i.hasNext()) {
            ((YapClass)i.next()).purge();
        }
    }

    final void readThis(Transaction a_trans, YapReader a_reader) {
        int classCount = a_reader.readInt();

        initTables(classCount);

        // Step 1 add all classes
        for (int i = classCount; i > 0; i--) {
            YapClass yapClass = new YapClass();
            int id = a_reader.readInt();
            yapClass.setID(i_stream, id);
            i_classes.add(yapClass);
            i_yapClassByID.put(id, yapClass);
            i_yapClassByBytes.put(yapClass.readName(a_trans), yapClass);
        }
    }

    YapClass readYapClass(YapClass yapClass, IClass a_class) {
        i_yapClassCreationDepth++;
        if (yapClass != null  && yapClass.stateUnread()) {
            yapClass.createConfigAndConstructor(i_yapClassByBytes, i_stream, a_class);
            IClass claxx = yapClass.classReflector();
            if(claxx != null){
                i_yapClassByClass.put(claxx, yapClass);
                yapClass.readThis();
                yapClass.checkChanges();
                i_initYapClassesOnUp.add(yapClass);
            }
        }
        i_yapClassCreationDepth--;
        initYapClassesOnUp();
        return yapClass;
    }

    void refreshClasses() {
        YapClassCollection rereader = new YapClassCollection(i_systemTrans);
        rereader.i_id = i_id;
        rereader.read(i_stream.getSystemTransaction());
        Iterator4 i = rereader.i_classes.iterator();
        while (i.hasNext()) {
            YapClass yc = (YapClass)i.next();
            if (i_yapClassByID.get(yc.getID()) == null) {
                i_classes.add(yc);
                i_yapClassByID.put(yc.getID(), yc);
                if(yc.stateUnread()){
                    i_yapClassByBytes.put(yc.readName(i_systemTrans), yc);
                }else{
                    i_yapClassByClass.put(yc.classReflector(), yc);
                }
            }
        }
        i = i_classes.iterator();
        while (i.hasNext()) {
            YapClass yc = (YapClass)i.next();
            yc.refresh();
        }
    }

    void reReadYapClass(YapClass yapClass){
        if(yapClass != null){
            reReadYapClass(yapClass.i_ancestor);
            yapClass.readName(i_systemTrans);
            yapClass.forceRead();
            yapClass.setStateClean();
            yapClass.bitFalse(YapConst.CHECKED_CHANGES);
            yapClass.bitFalse(YapConst.READING);
            yapClass.bitFalse(YapConst.CONTINUE);
            yapClass.bitFalse(YapConst.DEAD);
            yapClass.checkChanges();
        }
    }
    
    public StoredClass[] storedClasses() {
        Collection4 classes = new Collection4();
        Iterator4 i = i_classes.iterator();
        while (i.hasNext()) {
            YapClass yc = (YapClass)i.next();
            readYapClass(yc, null);
            if(yc.getJavaClass() == null){
                yc.forceRead();
            }
            classes.add(yc);
        }
        StoredClass[] sclasses = new StoredClass[classes.size()];
        classes.toArray(sclasses);
        return sclasses;
    }

    void writeThis(YapWriter a_writer) {
        a_writer.writeInt(i_classes.size());
        Iterator4 i = i_classes.iterator();
        while (i.hasNext()) {
            writeIDOf((YapClass)i.next(), a_writer);
        }
    }
    
    void yapClassRequestsInitOnUp(YapClass a_yc){
        if(i_yapClassCreationDepth == 0){
            a_yc.initOnUp(i_systemTrans);
        }else{
            i_initYapClassesOnUp.add(a_yc);
        }
    }

    void yapFields(final String a_field, final Visitor4 a_visitor) {
        YapClassCollectionIterator i = iterator();
        while (i.hasNext()) {
            final YapClass yc = i.nextClass();
            yc.forEachYapField(new Visitor4() {
                public void visit(Object obj) {
                    YapField yf = (YapField)obj;
                    if (yf.alive() && a_field.equals(yf.getName())) {
                        a_visitor.visit(new Object[] {yc, yf});
                    }
                }
            });
        }
    }

    // Debug info:

    //	public String toString(){
    //		String str = "";
    //		Iterator4 i = i_classes.iterator();
    //		while(i.hasNext()){
    //			YapClass yc = (YapClass)i.next();
    //			str += yc.getID() + " " + yc + "\r\n";
    //		}
    //		return str;
    //	}

}

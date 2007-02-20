/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.ext.StoredClass;
import com.db4o.foundation.ArrayIterator4;
import com.db4o.foundation.Collection4;
import com.db4o.foundation.Debug4;
import com.db4o.foundation.Hashtable4;
import com.db4o.foundation.Iterator4;
import com.db4o.foundation.MappingIterator;
import com.db4o.foundation.Queue4;
import com.db4o.foundation.Visitor4;
import com.db4o.reflect.ReflectClass;


/**
 * @exclude
 */
public final class ClassMetadataRepository extends PersistentBase {

    private Collection4 i_classes;
    private Hashtable4 i_creating;
    
    private final Transaction _systemTransaction;

    private Hashtable4 i_yapClassByBytes;
    private Hashtable4 i_yapClassByClass;
    private Hashtable4 i_yapClassByID;
    
    private int i_yapClassCreationDepth;
    private Queue4 i_initYapClassesOnUp;
	
	private final PendingClassInits _classInits; 


    ClassMetadataRepository(Transaction systemTransaction) {
        _systemTransaction = systemTransaction;
        i_initYapClassesOnUp = new Queue4();
		_classInits = new PendingClassInits(_systemTransaction);
    }

    public void addYapClass(ClassMetadata yapClass) {
        stream().setDirtyInSystemTransaction(this);
        i_classes.add(yapClass);
        if(yapClass.stateUnread()){
            i_yapClassByBytes.put(yapClass.i_nameBytes, yapClass);
        }else{
            i_yapClassByClass.put(yapClass.classReflector(), yapClass);
        }
        if (yapClass.getID() == 0) {
            yapClass.write(_systemTransaction);
        }
        i_yapClassByID.put(yapClass.getID(), yapClass);
    }
    
    private byte[] asBytes(String str){
        return stream().stringIO().write(str);
    }

    public void attachQueryNode(final String fieldName, final Visitor4 a_visitor) {
        ClassMetadataIterator i = iterator();
        while (i.moveNext()) {
            final ClassMetadata yc = i.currentClass();
            if(! yc.isInternal()){
                yc.forEachYapField(new Visitor4() {
                    public void visit(Object obj) {
                        FieldMetadata yf = (FieldMetadata)obj;
                        if(yf.canAddToQuery(fieldName)){
                            a_visitor.visit(new Object[] {yc, yf});
                        }
                    }
                });
            }
        }
    }

    void checkChanges() {
        Iterator4 i = i_classes.iterator();
        while (i.moveNext()) {
            ((ClassMetadata)i.current()).checkChanges();
        }
    }
    
    final boolean createYapClass(ClassMetadata a_yapClass, ReflectClass a_class) {
        i_yapClassCreationDepth++;
        ReflectClass superClass = a_class.getSuperclass();
        ClassMetadata superYapClass = null;
        if (superClass != null && ! superClass.equals(stream().i_handlers.ICLASS_OBJECT)) {
            superYapClass = produceYapClass(superClass);
        }
        boolean ret = stream().createYapClass(a_yapClass, a_class, superYapClass);
        i_yapClassCreationDepth--;
        initYapClassesOnUp();
        return ret;
    }

	public static void defrag(ReaderPair readers) {
        if (Deploy.debug) {
            readers.readBegin(Const4.YAPCLASSCOLLECTION);
        }
		int numClasses=readers.readInt();
		for(int classIdx=0;classIdx<numClasses;classIdx++) {
			readers.copyID();
		}
        if (Deploy.debug) {
            readers.readEnd();
        }
	}

	private void ensureAllClassesRead() {
		boolean allClassesRead=false;
    	while(!allClassesRead) {
	    	Collection4 unreadClasses=new Collection4();
			int numClasses=i_classes.size();
	        Iterator4 classIter = i_classes.iterator();
	        while(classIter.moveNext()) {
	        	ClassMetadata yapClass=(ClassMetadata)classIter.current();
	        	if(yapClass.stateUnread()) {
	        		unreadClasses.add(yapClass);
	        	}
	        }
	        Iterator4 unreadIter=unreadClasses.iterator();
	        while(unreadIter.moveNext()) {
	        	ClassMetadata yapClass=(ClassMetadata)unreadIter.current();
	        	readYapClass(yapClass,null);
	            if(yapClass.classReflector() == null){
	            	yapClass.forceRead();
	            }
	        }
	        allClassesRead=(i_classes.size()==numClasses);
    	}
		applyReadAs();
	}

    boolean fieldExists(String a_field) {
        ClassMetadataIterator i = iterator();
        while (i.moveNext()) {
            if (i.currentClass().getYapField(a_field) != null) {
                return true;
            }
        }
        return false;
    }

    public Collection4 forInterface(ReflectClass claxx) {
        Collection4 col = new Collection4();
        ClassMetadataIterator i = iterator();
        while (i.moveNext()) {
            ClassMetadata yc = i.currentClass();
            ReflectClass candidate = yc.classReflector();
            if(! candidate.isInterface()){
                if (claxx.isAssignableFrom(candidate)) {
                    col.add(yc);
                    Iterator4 j = new Collection4(col).iterator();
                    while (j.moveNext()) {
                        ClassMetadata existing = (ClassMetadata)j.current();
                        if(existing != yc){
                            ClassMetadata higher = yc.getHigherHierarchy(existing);
                            if (higher != null) {
                                if (higher == yc) {
                                    col.remove(existing);
                                }else{
                                    col.remove(yc);
                                }
                            }
                        }
                    }
                }
            }
        }
        return col;
    }

    public byte getIdentifier() {
        return Const4.YAPCLASSCOLLECTION;
    }
    
    ClassMetadata getActiveYapClass(ReflectClass a_class) {
        return (ClassMetadata)i_yapClassByClass.get(a_class);
    }
    
    ClassMetadata getYapClass (ReflectClass a_class) {
    	ClassMetadata yapClass = (ClassMetadata)i_yapClassByClass.get(a_class);
        if (yapClass != null) {
        	return yapClass;
        }
        yapClass = (ClassMetadata)i_yapClassByBytes.remove(getNameBytes(a_class.getName()));
        readYapClass(yapClass, a_class);
        return yapClass;
    }

    ClassMetadata produceYapClass(ReflectClass a_class) {
    	
    	ClassMetadata yapClass = getYapClass(a_class);
    	
        if (yapClass != null ) {
            return yapClass;
        }
        
        yapClass = (ClassMetadata)i_creating.get(a_class);
        
        if(yapClass != null){
            return yapClass;
        }
        
        yapClass = new ClassMetadata(stream(), a_class);
        
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
            yapClass.write(stream().getSystemTransaction());
            id = yapClass.getID();
        }
        
        if(i_yapClassByID.get(id) == null){
            i_yapClassByID.put(id, yapClass);
            addMembers = true;
        }
        
        if(addMembers || yapClass.i_fields == null){
			_classInits.process(yapClass);
        }
        
        i_creating.remove(a_class);
        
        stream().setDirtyInSystemTransaction(this);
        
        return yapClass;
    }    
    
	ClassMetadata getYapClass(int id) {
        return readYapClass((ClassMetadata)i_yapClassByID.get(id), null);
    }
	
    public ClassMetadata getYapClass(String a_name) {
        ClassMetadata yapClass = (ClassMetadata)i_yapClassByBytes.remove(getNameBytes(a_name));
        readYapClass(yapClass, null);
        if (yapClass == null) {
            ClassMetadataIterator i = iterator();
            while (i.moveNext()) {
                yapClass = (ClassMetadata)i.current();
                if (a_name.equals(yapClass.getName())) {
                    readYapClass(yapClass, null);
                    return yapClass;
                }
            }
            return null;
        }
        return yapClass;
    }
    
    public int getYapClassID(String name){
        ClassMetadata yc = (ClassMetadata)i_yapClassByBytes.get(getNameBytes(name));
        if(yc != null){
            return yc.getID();
        }
        return 0;
    }

	byte[] getNameBytes(String name) {		
		return asBytes(resolveAliasRuntimeName(name));
	}

	private String resolveAliasRuntimeName(String name) {
		return stream().configImpl().resolveAliasRuntimeName(name);
	}

    void initOnUp(Transaction systemTrans) {
        i_yapClassCreationDepth++;
        systemTrans.stream().showInternalClasses(true);
        try {
	        Iterator4 i = i_classes.iterator();
	        while (i.moveNext()) {
	            ((ClassMetadata)i.current()).initOnUp(systemTrans);
	        }
        } finally {
        	systemTrans.stream().showInternalClasses(false);
        }
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
            ClassMetadata yc = (ClassMetadata)i_initYapClassesOnUp.next();
            while(yc != null){
                yc.initOnUp(_systemTransaction);
                yc = (ClassMetadata)i_initYapClassesOnUp.next();
            }
        }
    }
    
    public ClassMetadataIterator iterator(){
        return new ClassMetadataIterator(this, new ArrayIterator4(i_classes.toArray()));
    } 

    private static class ClassIDIterator extends MappingIterator {

		public ClassIDIterator(Collection4 classes) {
			super(classes.iterator());
		}
    	
    	protected Object map(Object current) {
    		return new Integer(((ClassMetadata)current).getID());
    	}
    }
    
    public Iterator4 ids(){
        return new ClassIDIterator(i_classes);
    } 

    public int ownLength() {
        return Const4.OBJECT_LENGTH
            + Const4.INT_LENGTH
            + (i_classes.size() * Const4.ID_LENGTH);
    }

    void purge() {
        Iterator4 i = i_classes.iterator();
        while (i.moveNext()) {
            ((ClassMetadata)i.current()).purge();
        }
    }

    public final void readThis(Transaction a_trans, Buffer a_reader) {
		int classCount = a_reader.readInt();

		initTables(classCount);

		ObjectContainerBase stream = stream();
		int[] ids = new int[classCount];

		for (int i = 0; i < classCount; ++i) {
			ids[i] = a_reader.readInt();
		}
		StatefulBuffer[] yapWriters = stream.readWritersByIDs(a_trans, ids);

		for (int i = 0; i < classCount; ++i) {
			ClassMetadata yapClass = new ClassMetadata(stream, null);
			yapClass.setID(ids[i]);
			i_classes.add(yapClass);
			i_yapClassByID.put(ids[i], yapClass);
			byte[] name = yapClass.readName1(a_trans, yapWriters[i]);
			i_yapClassByBytes.put(name, yapClass);
		}

		applyReadAs();

	}

	Hashtable4 classByBytes(){
    	return i_yapClassByBytes;
    }
    
    private void applyReadAs(){
        final Hashtable4 readAs = stream().configImpl().readAs(); 
        readAs.forEachKey(new Visitor4() {
            public void visit(Object a_object) {
                String dbName = (String)a_object;
                byte[] dbbytes = getNameBytes(dbName);
                String useName = (String)readAs.get(dbName);
                byte[] useBytes = getNameBytes(useName);
                if(classByBytes().get(useBytes) == null){
                    ClassMetadata yc = (ClassMetadata)classByBytes().get(dbbytes);
                    if(yc != null){
                        yc.i_nameBytes = useBytes;
                        yc.setConfig(stream().configImpl().configClass(dbName));
                        classByBytes().put(dbbytes, null);
                        classByBytes().put(useBytes, yc);
                    }
                }
            }
        });
    }

    public ClassMetadata readYapClass(ClassMetadata yapClass, ReflectClass a_class) {
    	if(yapClass == null){
    		return null;
    	}
        if (! yapClass.stateUnread()) {
            return yapClass;
        }
        i_yapClassCreationDepth++;
        yapClass.createConfigAndConstructor(i_yapClassByBytes, stream(), a_class);
        ReflectClass claxx = yapClass.classReflector();
        if(claxx != null){
            i_yapClassByClass.put(claxx, yapClass);
            yapClass.readThis();
            yapClass.checkChanges();
            i_initYapClassesOnUp.add(yapClass);
        }
        i_yapClassCreationDepth--;
        initYapClassesOnUp();
        return yapClass;
    }

    public void refreshClasses() {
        ClassMetadataRepository rereader = new ClassMetadataRepository(_systemTransaction);
        rereader.i_id = i_id;
        rereader.read(stream().getSystemTransaction());
        Iterator4 i = rereader.i_classes.iterator();
        while (i.moveNext()) {
            ClassMetadata yc = (ClassMetadata)i.current();
            if (i_yapClassByID.get(yc.getID()) == null) {
                i_classes.add(yc);
                i_yapClassByID.put(yc.getID(), yc);
                if(yc.stateUnread()){
                    i_yapClassByBytes.put(yc.readName(_systemTransaction), yc);
                }else{
                    i_yapClassByClass.put(yc.classReflector(), yc);
                }
            }
        }
        i = i_classes.iterator();
        while (i.moveNext()) {
            ClassMetadata yc = (ClassMetadata)i.current();
            yc.refresh();
        }
    }

    void reReadYapClass(ClassMetadata yapClass){
        if(yapClass != null){
            reReadYapClass(yapClass.i_ancestor);
            yapClass.readName(_systemTransaction);
            yapClass.forceRead();
            yapClass.setStateClean();
            yapClass.bitFalse(Const4.CHECKED_CHANGES);
            yapClass.bitFalse(Const4.READING);
            yapClass.bitFalse(Const4.CONTINUE);
            yapClass.bitFalse(Const4.DEAD);
            yapClass.checkChanges();
        }
    }
    
    public StoredClass[] storedClasses() {
    	ensureAllClassesRead();
        StoredClass[] sclasses = new StoredClass[i_classes.size()];
        i_classes.toArray(sclasses);
        return sclasses;
    }

    public void writeAllClasses(){
        StoredClass[] storedClasses = storedClasses();
        for (int i = 0; i < storedClasses.length; i++) {
            ClassMetadata yc = (ClassMetadata)storedClasses[i];
            yc.setStateDirty();
        }
        
        for (int i = 0; i < storedClasses.length; i++) {
            ClassMetadata yc = (ClassMetadata)storedClasses[i];
            yc.write(_systemTransaction);
        }
    }

    public void writeThis(Transaction trans, Buffer a_writer) {
        a_writer.writeInt(i_classes.size());
        Iterator4 i = i_classes.iterator();
        while (i.moveNext()) {
            a_writer.writeIDOf(trans, i.current());
        }
    }

	public String toString(){
        if(! Debug4.prettyToStrings){
            return super.toString();
        }
		String str = "Active:\n";
		Iterator4 i = i_classes.iterator();
		while(i.moveNext()){
			ClassMetadata yc = (ClassMetadata)i.current();
			str += yc.getID() + " " + yc + "\n";
		}
		return str;
	}

    ObjectContainerBase stream() {
        return _systemTransaction.stream();
    }
    
    public void setID(int a_id) {
    	if (stream().isClient()) {
    		super.setID(a_id);
    		return;
    	}
    	
        if(i_id == 0) {        	
			systemData().classCollectionID(a_id);
        }
        super.setID(a_id);
    }

	private SystemData systemData() {
		return _systemTransaction.i_file.systemData();
	}

}

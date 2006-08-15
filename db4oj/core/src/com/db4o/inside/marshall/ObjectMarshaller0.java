/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;

/**
 * @exclude
 */
class ObjectMarshaller0 extends ObjectMarshaller {
    
    public void addFieldIndices(YapClass yc, ObjectHeaderAttributes attributes, YapWriter writer, boolean isNew) {
        int fieldCount = writer.readInt();
        for (int i = 0; i < fieldCount; i++) {
            yc.i_fields[i].addFieldIndex(_family, writer, isNew);
        }
        if (yc.i_ancestor != null) {
            addFieldIndices(yc.i_ancestor, attributes, writer, isNew);
        }
    }
    
    public TreeInt collectFieldIDs(TreeInt tree, YapClass yc, ObjectHeaderAttributes attributes, YapWriter reader, String name) {
        int length = yc.readFieldCount(reader);
        for (int i = 0; i < length; i++) {
            if (name.equals(yc.i_fields[i].getName())) {
                tree = yc.i_fields[i].collectIDs(_family, tree, reader);
            } else {
                yc.i_fields[i].incrementOffset(reader);
            }
        }
        if (yc.i_ancestor != null) {
            return collectFieldIDs(tree, yc.i_ancestor, attributes, reader, name);
        }
        return tree;
    }
    
    public void deleteMembers(YapClass yc, ObjectHeaderAttributes attributes, YapWriter a_bytes, int a_type, boolean isUpdate){
        int length = yc.readFieldCount(a_bytes);
        for (int i = 0; i < length; i++) {
            yc.i_fields[i].delete(_family, a_bytes, isUpdate);
        }
        if (yc.i_ancestor != null) {
            deleteMembers(yc.i_ancestor, attributes, a_bytes, a_type, isUpdate);
        }
    }
    
    public boolean findOffset(YapClass yc, ObjectHeaderAttributes attributes, YapReader a_bytes, YapField a_field) {
        int length = Debug.atHome ? yc.readFieldCountSodaAtHome(a_bytes) : yc.readFieldCount(a_bytes);
        for (int i = 0; i < length; i++) {
            if (yc.i_fields[i] == a_field) {
                return true;
            }
            a_bytes.incrementOffset(yc.i_fields[i].linkLength());
        }
        if (yc.i_ancestor == null) {
            return false;
        }
        return findOffset(yc.i_ancestor, attributes, a_bytes, a_field);
    }
    
    protected final int headerLength(){
        return YapConst.OBJECT_LENGTH + YapConst.ID_LENGTH;
    }
    
    public void instantiateFields(YapClass yc, ObjectHeaderAttributes attributes, YapObject a_yapObject, Object a_onObject, YapWriter a_bytes) {
        int length = yc.readFieldCount(a_bytes);
        try {
            for (int i = 0; i < length; i++) {
                yc.i_fields[i].instantiate(_family, a_yapObject, a_onObject, a_bytes);
            }
            if (yc.i_ancestor != null) {
                instantiateFields(yc.i_ancestor, attributes, a_yapObject, a_onObject, a_bytes);
            }
        } catch (CorruptionException ce) {
        }
    }
    
    private int linkLength(YapClass yc, YapObject yo) {
        int length = YapConst.INT_LENGTH;
        if (yc.i_fields != null) {
            for (int i = 0; i < yc.i_fields.length; i++) {
                length += linkLength(yc.i_fields[i], yo);
            }
        }
        if (yc.i_ancestor != null) {
            length += linkLength(yc.i_ancestor, yo);
        }
        return length;
    }
    
    protected int linkLength(YapField yf, YapObject yo){
        return yf.linkLength();
    }
    
    private void marshall(YapClass yapClass, YapObject a_yapObject, Object a_object, YapWriter writer, boolean a_new) {
        marshallDeclaredFields(yapClass, a_yapObject, a_object, writer, a_new);
        if (Deploy.debug) {
            writer.writeEnd();
            writer.debugCheckBytes();
        }
    }
    
    private void marshallDeclaredFields(YapClass yapClass, YapObject a_yapObject, Object a_object, YapWriter a_bytes, boolean a_new) {
        Config4Class config = yapClass.configOrAncestorConfig();
        a_bytes.writeInt(yapClass.i_fields.length);
        for (int i = 0; i < yapClass.i_fields.length; i++) {
            Object obj = yapClass.i_fields[i].getOrCreate(a_bytes.getTransaction(), a_object);
            if (obj instanceof Db4oTypeImpl) {
                obj = ((Db4oTypeImpl)obj).storedTo(a_bytes.getTransaction());
            }
            yapClass.i_fields[i].marshall(a_yapObject, obj, _family, a_bytes, config, a_new);
        }
        if (yapClass.i_ancestor != null) {
            marshallDeclaredFields(yapClass.i_ancestor, a_yapObject, a_object, a_bytes, a_new);
        }
    }
    
    protected int marshalledLength(YapField yf, YapObject yo){
        return 0;
    }

    public YapWriter marshallNew(Transaction a_trans, YapObject yo, int a_updateDepth){
        
        YapWriter writer = createWriterForNew(a_trans, yo, a_updateDepth, objectLength(yo));
        
        YapClass yc = yo.getYapClass();
        Object obj = yo.getObject();
        
        if(yc.isPrimitive()){
            ((YapClassPrimitive)yc).i_handler.writeNew(MarshallerFamily.current(), obj, false, writer, true, false);
            if (Deploy.debug) {
                writer.writeEnd();
                writer.debugCheckBytes();
            }
        }else{
            writer.writeInt(yc.getID());
            yc.checkUpdateDepth(writer);
            marshall(yc, yo, obj, writer, true);
        }
        return writer;
    }

    public void marshallUpdate(
        Transaction trans,
        int updateDepth,
        YapObject yapObject,
        Object obj
        ) {
        
        YapWriter writer = createWriterForUpdate(trans,updateDepth, yapObject.getID(), 0, objectLength(yapObject));
        
        YapClass yapClass = yapObject.getYapClass();
        
        yapClass.checkUpdateDepth(writer);
        
        writer.writeInt(yapClass.getID());
        marshall(yapClass, yapObject, obj, writer, false);
        
        marshallUpdateWrite(trans, yapObject, obj, writer);
    }

    private int objectLength(YapObject yo) {
        return headerLength() + linkLength(yo.getYapClass(), yo);
    }

    public ObjectHeaderAttributes readHeaderAttributes(YapReader reader) {
        return null;
    }

    public Object readIndexEntry(YapClass yc, ObjectHeaderAttributes attributes, YapField yf, YapWriter reader) {
        if(yc == null){
            return null;
        }
        
        if(! findOffset(yc, attributes, reader, yf)){
            return null;
        }
        
        return yf.readIndexEntry(_family, reader);
    }
    
    public void readVirtualAttributes(Transaction trans,  YapClass yc, YapObject yo, ObjectHeaderAttributes attributes, YapReader reader){
        int length = yc.readFieldCount(reader);
        for (int i = 0; i < length; i++) {
            yc.i_fields[i].readVirtualAttribute(trans, reader, yo);
        }
        if (yc.i_ancestor != null) {
            readVirtualAttributes(trans, yc.i_ancestor, yo, attributes, reader);
        }
    }

}

/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;

/**
 * @exclude
 */
public class ObjectMarshaller1 extends ObjectMarshaller{
    
    static final boolean NULLBITMAP = false;
    
    static final byte VERSION = (byte)1;
    
    public void addFieldIndices(YapClass yc, ObjectHeaderAttributes attributes, YapWriter writer, boolean isNew) {
        addDeclaredFieldIndices(yc, (ObjectHeaderAttributes1) attributes, writer, 0, isNew);
    }

    // FIXME: remove, when Bitmap format is used for writing
    private void inc(YapField yf, YapReader writer){
        if(! NULLBITMAP){
            writer.incrementOffset(yf.linkLength());
        }
    }
    
    private void addDeclaredFieldIndices(YapClass yc, ObjectHeaderAttributes1 attributes, YapWriter writer, int fieldIndex, boolean isNew) {
        int fieldCount = writer.readInt();
        for (int i = 0; i < fieldCount; i++) {
            if(attributes._nullBitMap.isTrue(fieldIndex)){
                yc.i_fields[i].addIndexEntry(writer.getTransaction(), writer.getID(), null);
                inc(yc.i_fields[i], writer);
            }else{
                yc.i_fields[i].addFieldIndex(_family, writer, isNew);
            }
            fieldIndex ++;
        }
        if (yc.i_ancestor != null) {
            addDeclaredFieldIndices(yc.i_ancestor, attributes, writer, fieldIndex, isNew);
        }
    }
    
    public void deleteMembers(YapClass yc, ObjectHeaderAttributes attributes, YapWriter a_bytes, int a_type, boolean isUpdate){
        deleteDeclaredMembers(yc, (ObjectHeaderAttributes1) attributes, a_bytes, a_type, 0, isUpdate);
    }
    
    public void deleteDeclaredMembers(YapClass yc, ObjectHeaderAttributes1 attributes, YapWriter a_bytes, int a_type, int fieldIndex, boolean isUpdate){
        int length = yc.readFieldLength(a_bytes);
        for (int i = 0; i < length; i++) {
            if(attributes._nullBitMap.isTrue(fieldIndex)){
                yc.i_fields[i].removeIndexEntry(a_bytes.getTransaction(), a_bytes.getID(), null);
                inc(yc.i_fields[i], a_bytes);
            }else{
                yc.i_fields[i].delete(_family, a_bytes, isUpdate);
            }
            fieldIndex ++;
        }
        if (yc.i_ancestor != null) {
            deleteDeclaredMembers(yc.i_ancestor, attributes, a_bytes, a_type, fieldIndex, isUpdate);
        }
    }
    
    public boolean findOffset(YapClass yc, ObjectHeaderAttributes attributes, YapReader a_bytes, YapField a_field) {
        return findDeclaredOffset(yc, (ObjectHeaderAttributes1) attributes, a_bytes, a_field, 0);
    }
    
    public boolean findDeclaredOffset(YapClass yc, ObjectHeaderAttributes1 attributes, YapReader a_bytes, YapField a_field, int fieldIndex) {
        int length = Debug.atHome ? yc.readFieldLengthSodaAtHome(a_bytes) : yc.readFieldLength(a_bytes);
        for (int i = 0; i < length; i++) {
            
            if (yc.i_fields[i] == a_field) {
                return ! attributes._nullBitMap.isTrue(fieldIndex); 
            }
            if(attributes._nullBitMap.isTrue(fieldIndex)){
                inc(yc.i_fields[i], a_bytes);
            }else{
                a_bytes.incrementOffset(yc.i_fields[i].linkLength());
            }
            
            fieldIndex ++;
        }
        if (yc.i_ancestor == null) {
            return false;
        }
        return findDeclaredOffset(yc.i_ancestor, attributes, a_bytes, a_field, fieldIndex);
    }
    
    protected final int headerLength(){
        return YapConst.OBJECT_LENGTH 
            + YapConst.YAPID_LENGTH  // YapClass ID 
            + 1; // Marshaller Version 
    }
    
    public void instantiateFields(YapClass yc, ObjectHeaderAttributes attributes, YapObject a_yapObject, Object a_onObject, YapWriter a_bytes) {
        instantiateDeclaredFields(yc, (ObjectHeaderAttributes1) attributes, a_yapObject, a_onObject, a_bytes, 0);
    }
    
    public void instantiateDeclaredFields(YapClass yc, ObjectHeaderAttributes1 attributes, YapObject a_yapObject, Object a_onObject, YapWriter a_bytes, int fieldIndex) {
        int length = yc.readFieldLength(a_bytes);
        try {
            for (int i = 0; i < length; i++) {
                
                if(attributes._nullBitMap.isTrue(fieldIndex)){
                    yc.i_fields[i].set(a_onObject, null);
                    inc(yc.i_fields[i], a_bytes);
                }else{
                    yc.i_fields[i].instantiate(_family, a_yapObject, a_onObject, a_bytes);
                }
                
                fieldIndex ++;
            }
            if (yc.i_ancestor != null) {
                instantiateDeclaredFields(yc.i_ancestor, attributes, a_yapObject, a_onObject, a_bytes, fieldIndex);
            }
        } catch (CorruptionException ce) {
        }
    }

    
    protected int linkLength(YapField yf, YapObject yo){
        return yf.linkLength();
    }

    private void marshall(YapClass yapClass, YapObject a_yapObject, Object a_object, ObjectHeaderAttributes1 attributes, YapWriter a_bytes, int fieldIndex, boolean a_new) {
        Config4Class config = yapClass.configOrAncestorConfig();
        Transaction trans = a_bytes.getTransaction();
        int numFields = yapClass.i_fields.length;
        a_bytes.writeInt(numFields);
        for (int i = 0; i < numFields; i++) {
            
            YapField yf = yapClass.i_fields[i];
            
            if(true || ! attributes._nullBitMap.isTrue(fieldIndex)){
                
                Object obj = yf.getOrCreate(trans, a_object);
                if (obj instanceof Db4oTypeImpl) {
                    obj = ((Db4oTypeImpl)obj).storedTo(trans);
                }
                yf.marshall(a_yapObject, obj, a_bytes, config, a_new);
            } else{
                yf.addIndexEntry(trans, a_bytes.getID(), null);
                inc(yapClass.i_fields[i], a_bytes);
            }
                
            fieldIndex ++;
        }
        
        
        if (yapClass.i_ancestor != null) {
            marshall(yapClass.i_ancestor, a_yapObject, a_object, attributes, a_bytes,fieldIndex, a_new);
        }
    }
    
    private int marshalledLength(YapClass yc, YapObject yo, ObjectHeaderAttributes1 attributes, int fieldIndex) {
        int length = 0;
        if (yc.i_fields != null) {
            for (int i = 0; i < yc.i_fields.length; i++) {
                length += marshalledLength(yc.i_fields[i], yo, attributes, fieldIndex);
                fieldIndex ++;
            }
        }
        if (yc.i_ancestor != null) {
            length += marshalledLength(yc.i_ancestor, yo, attributes, fieldIndex);
        }
        return length;
    }
    
    private int marshalledLength(YapField yf, YapObject yo, ObjectHeaderAttributes1 attributes, int fieldIndex){
        Transaction trans = yo.getTrans();
        Object parentObject = yo.getObject();
        Object child = yf.getOn(trans, parentObject);
        
        if( child == null && yf.canUseNullBitmap()){
            attributes._nullBitMap.setTrue(fieldIndex);
            if(NULLBITMAP){
                return 0; 
            }
        }
        
        return yf.marshalledLength(_family, child);
    }

    public YapWriter marshallNew(Transaction a_trans, YapObject yo, int a_updateDepth){
        
        YapClass yc = yo.getYapClass();
        
        int classFieldCount = yc.fieldCount();
        
        ObjectHeaderAttributes1 attributes = new ObjectHeaderAttributes1(yc);
        
        YapWriter writer = createWriter(a_trans, yo, attributes, a_updateDepth);
        
        Object obj = yo.getObject();
        
        if(yc.isPrimitive()){

            // FIXME: Unused int, this code here is doomed anyway.
            //        When this code is removed, consider to join the
            //        similarities of this method with marshallUpdate
            
            throw new Db4oException("Should never happen.");
            
//            writer.writeInt(0);
//            
//            ((YapClassPrimitive)yc).marshallNew(yo, writer, obj);
            
            
        }else{
            writer.writeInt(- yc.getID());
            attributes.write(writer);
            
            yc.checkUpdateDepth(writer);
            marshall(yc, yo, obj, attributes, writer, 0, true);
        }

        if (Deploy.debug) {
            writer.writeEnd();
            writer.debugCheckBytes();
        }
        
        return writer;
    }
    
    public void marshallUpdate(
        Transaction a_trans,
        YapClass yapClass,
        int a_id,
        int a_updateDepth,
        YapObject a_yapObject,
        Object a_object
        ) {
        
        ObjectHeaderAttributes1 attributes = new ObjectHeaderAttributes1(yapClass);

        int length = objectLength(a_yapObject, attributes);
        
        YapWriter writer = createWriter(a_trans, a_yapObject, attributes, a_updateDepth, a_id, 0, length);
        
        if(a_trans.i_file != null){
            // Running in single mode or on server.
            // We need the slot now, so indexes can adjust to address.
            a_trans.i_file.getSlotForUpdate(writer);
        }
        
        yapClass.checkUpdateDepth(writer);
        
        writer.writeInt(- yapClass.getID());
        attributes.write(writer);
        
        marshall(yapClass, a_yapObject, a_object, attributes, writer,0, false);
        
        marshallUpdateWrite(a_trans, yapClass, a_yapObject, a_object, writer);
    }
    
    protected int objectLength(YapObject yo, ObjectHeaderAttributes attributes){
        return alignedBaseLength(yo, attributes) + marshalledLength(yo.getYapClass(), yo, (ObjectHeaderAttributes1)attributes, 0);
    }
    
    public ObjectHeaderAttributes readHeaderAttributes(YapReader reader) {
        return new ObjectHeaderAttributes1(reader);
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



}

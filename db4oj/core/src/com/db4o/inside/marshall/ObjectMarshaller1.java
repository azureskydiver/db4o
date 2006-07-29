/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;

/**
 * @exclude
 */
public class ObjectMarshaller1 extends ObjectMarshaller{
    
    public void addFieldIndices(YapClass yc, ObjectHeaderAttributes attributes, YapWriter writer, boolean isNew) {
        addDeclaredFieldIndices(yc, (ObjectHeaderAttributes1) attributes, writer, 0, isNew);
    }
    
    private void addDeclaredFieldIndices(YapClass yc, ObjectHeaderAttributes1 attributes, YapWriter writer, int fieldIndex, boolean isNew) {
        int fieldCount = writer.readInt();
        for (int i = 0; i < fieldCount; i++) {
            if(attributes.isNull(fieldIndex)){
                yc.i_fields[i].addIndexEntry(writer.getTransaction(), writer.getID(), null);
            }else{
                yc.i_fields[i].addFieldIndex(_family, writer, isNew);
            }
            fieldIndex ++;
        }
        if (yc.i_ancestor != null) {
            addDeclaredFieldIndices(yc.i_ancestor, attributes, writer, fieldIndex, isNew);
        }
    }
    
    public TreeInt collectFieldIDs(TreeInt tree, YapClass yc, ObjectHeaderAttributes attributes, YapWriter reader, String name) {
        return collectDeclaredFieldIDs(tree, yc, (ObjectHeaderAttributes1) attributes, reader, name, 0);
    }
    
    public TreeInt collectDeclaredFieldIDs(TreeInt tree, YapClass yc, ObjectHeaderAttributes1 attributes, YapWriter reader, String name, int fieldIndex) {
        int length = yc.readFieldCount(reader);
        for (int i = 0; i < length; i++) {
            if(! attributes.isNull(fieldIndex)){
                if (name.equals(yc.i_fields[i].getName())) {
                    tree = yc.i_fields[i].collectIDs(_family, tree, reader);
                } else {
                    yc.i_fields[i].incrementOffset(reader);
                }
            }
            fieldIndex ++;
        }
        if (yc.i_ancestor != null) {
            return collectDeclaredFieldIDs(tree, yc.i_ancestor, attributes, reader, name, fieldIndex);
        }
        return tree;
    }
    

    
    public void deleteMembers(YapClass yc, ObjectHeaderAttributes attributes, YapWriter a_bytes, int a_type, boolean isUpdate){
        deleteDeclaredMembers(yc, (ObjectHeaderAttributes1) attributes, a_bytes, a_type, 0, isUpdate);
    }
    
    public void deleteDeclaredMembers(YapClass yc, ObjectHeaderAttributes1 attributes, YapWriter a_bytes, int a_type, int fieldIndex, boolean isUpdate){
        int length = yc.readFieldCount(a_bytes);
        for (int i = 0; i < length; i++) {
            if(attributes.isNull(fieldIndex)){
                yc.i_fields[i].removeIndexEntry(a_bytes.getTransaction(), a_bytes.getID(), null);
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
        int fieldCount = Debug.atHome ? yc.readFieldCountSodaAtHome(a_bytes) : yc.readFieldCount(a_bytes);
        for (int i = 0; i < fieldCount; i++) {
            
            if (yc.i_fields[i] == a_field) {
                return ! attributes.isNull(fieldIndex); 
            }
            if(! attributes.isNull(fieldIndex)){
                a_bytes.incrementOffset(yc.i_fields[i].linkLength());
            }
            
            fieldIndex ++;
        }
        if (yc.i_ancestor == null) {
            return false;
        }
        return findDeclaredOffset(yc.i_ancestor, attributes, a_bytes, a_field, fieldIndex);
    }
    
    public void instantiateFields(YapClass yc, ObjectHeaderAttributes attributes, YapObject a_yapObject, Object a_onObject, YapWriter a_bytes) {
        instantiateDeclaredFields(yc, (ObjectHeaderAttributes1) attributes, a_yapObject, a_onObject, a_bytes, 0);
    }
    
    public void instantiateDeclaredFields(YapClass yc, ObjectHeaderAttributes1 attributes, YapObject a_yapObject, Object a_onObject, YapWriter a_bytes, int fieldIndex) {
        int fieldCount = yc.readFieldCount(a_bytes);
        try {
            for (int i = 0; i < fieldCount; i++) {
                
                if(attributes.isNull(fieldIndex)){
                    yc.i_fields[i].set(a_onObject, null);
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

    
    private void marshall(YapObject yo, Object obj, ObjectHeaderAttributes1 attributes, YapWriter writer, int fieldIndex, boolean isNew) {
        YapClass yc = yo.getYapClass();
        writer.writeInt(- yc.getID());
        attributes.write(writer);
        yc.checkUpdateDepth(writer);
        marshallDeclaredFields(yc, yo, obj, attributes, writer, fieldIndex, isNew);
        if (Deploy.debug) {
            writer.writeEnd();
            writer.debugCheckBytes();
        }
    }
    
    private void marshallDeclaredFields(YapClass yc, YapObject yo, Object obj, ObjectHeaderAttributes1 attributes, YapWriter writer, int fieldIndex, boolean isNew) {
        Config4Class config = yc.configOrAncestorConfig();
        Transaction trans = writer.getTransaction();
        
        int fieldCount = yc.i_fields.length;
        writer.writeInt(fieldCount);
        for (int i = 0; i < fieldCount; i++) {
            
            YapField yf = yc.i_fields[i];
            
            if(! attributes.isNull(fieldIndex)){
                
                Object child = yf.getOrCreate(trans, obj);
                if (child instanceof Db4oTypeImpl) {
                    child = ((Db4oTypeImpl)child).storedTo(trans);
                }
                yf.marshall(yo, child, _family, writer, config, isNew);
            } else{
                yf.addIndexEntry(trans, writer.getID(), null);
            }
                
            fieldIndex ++;
        }
        
        if (yc.i_ancestor != null) {
            marshallDeclaredFields(yc.i_ancestor, yo, obj, attributes, writer,fieldIndex, isNew);
        }
    }
    
    public YapWriter marshallNew(Transaction a_trans, YapObject yo, int a_updateDepth){
        
        ObjectHeaderAttributes1 attributes = new ObjectHeaderAttributes1(yo);
        
        YapWriter writer = createWriterForNew(
            a_trans, 
            yo, 
            a_updateDepth, 
            attributes.objectLength());
        
        marshall(yo, yo.getObject(), attributes, writer, 0, true);
        
        return writer;
    }
    
    public void marshallUpdate(
        Transaction trans,
        int updateDepth,
        YapObject yo,
        Object obj
        ) {
        
        ObjectHeaderAttributes1 attributes = new ObjectHeaderAttributes1(yo);
        
        YapWriter writer = createWriterForUpdate(
            trans, 
            updateDepth, 
            yo.getID(), 
            0, 
            attributes.objectLength());
        
        if(trans.i_file != null){
            // Running in single mode or on server.
            // We need the slot now, so indexes can adjust to address.
            trans.i_file.getSlotForUpdate(writer);
        }
        
        marshall(yo, obj, attributes, writer,0, false);
        
        marshallUpdateWrite(trans, yo, obj, writer);
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
    
    public void readVirtualAttributes(Transaction trans,  YapClass yc, YapObject yo, ObjectHeaderAttributes attributes, YapReader reader){
        readVirtualAttributesDeclared(trans, yc, yo, (ObjectHeaderAttributes1) attributes, reader, 0);
    }

    private void readVirtualAttributesDeclared(Transaction trans,  YapClass yc, YapObject yo, ObjectHeaderAttributes1 attributes, YapReader reader, int fieldIndex){
        int fieldCount = yc.readFieldCount(reader);
        for (int i = 0; i < fieldCount; i++) {
            if(! attributes.isNull(fieldIndex)){
                yc.i_fields[i].readVirtualAttribute(trans, reader, yo);
            }
            fieldIndex ++;
        }
        if (yc.i_ancestor != null) {
            readVirtualAttributesDeclared(trans, yc.i_ancestor, yo, attributes, reader, fieldIndex);
        }
    }

}

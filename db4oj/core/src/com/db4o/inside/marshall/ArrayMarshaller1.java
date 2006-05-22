/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;


class ArrayMarshaller1 extends ArrayMarshaller{
    
    private int calculateLength(YapArray arrayHandler, Object obj){
        
        TypeHandler4 typeHandler = arrayHandler.i_handler;
        
        int length = arrayHandler.objectLength(obj);
        
        if(! typeHandler.hasFixedLength()){
            Object[] all = arrayHandler.allElements(obj);
            for (int i = 0; i < all.length; i++) {
                Object object = all[i];
                length += typeHandler.marshalledLength(object);
            }
        }
        
        return length;
    }
    
    public void deleteEmbedded(YapArray arrayHandler, YapWriter reader) {
        
        int address = reader.readInt();
        int length = reader.readInt();
        if (address <= 0) {
            return;
        }
        
        YapStream stream = reader.getStream();
        Transaction trans = reader.getTransaction();
        TypeHandler4 typeHandler = arrayHandler.i_handler;
        
        YapWriter subReader = null;
        
        if (reader.cascadeDeletes() > 0 && typeHandler instanceof YapClass) {
            if(typeHandler.isSecondClass() == YapConst.YES){
                subReader = reader;
            }else{
                subReader = stream.readObjectWriterByAddress(trans,address,length);
            }
        }
                
        if (subReader != null) {
            if (Deploy.debug) {
                subReader.readBegin(arrayHandler.identifier());
            }
            subReader.setCascadeDeletes(reader.cascadeDeletes());
            for (int i = arrayHandler.elementCount(trans, subReader); i > 0; i--) {
                arrayHandler.i_handler.deleteEmbedded(_family, subReader);
            }
        }
        
        if(typeHandler.isSecondClass() != YapConst.YES){
            trans.slotFreeOnCommit(address, address, length);
        }
    }
    
    
    private int headerLength(){
        return YapConst.YAPINT_LENGTH * 2  // type info, count 
             + YapConst.OBJECT_LENGTH;
    }
    
    public int marshalledLength(YapArray arrayHandler, Object obj){
        
        TypeHandler4 typeHandler = arrayHandler.i_handler;
        
        if(typeHandler.isSecondClass() == YapConst.NO){
            return 0;
        }
        
        if(typeHandler.isSecondClass() == YapConst.UNKNOWN){
            
            // TODO: implement any arrays
            
            return 0;
        }
        
        if(typeHandler.isSecondClass() == YapConst.YES){
            return calculateLength(arrayHandler, obj);
        }
        
        return 0;
    }
    
    public Object read(YapArray arrayHandler,  YapWriter reader) throws CorruptionException{
        TypeHandler4 typeHandler = arrayHandler.i_handler;
        if(typeHandler.isSecondClass() == YapConst.YES){
            return readEmbedded(arrayHandler, reader);
        }else{
            return readLinked(arrayHandler, reader);
        }
    }
    
    public Object readEmbedded(YapArray arrayHandler, YapWriter reader) throws CorruptionException{
        int linkOffSet = reader.preparePayloadRead();
        Object array = arrayHandler.read1(_family, reader);
        reader._offset = linkOffSet;
        return array;
    }
    
    public Object readLinked(YapArray arrayHandler, YapWriter reader) throws CorruptionException{
        YapWriter bytes = reader.readEmbeddedObject();
        if (bytes == null) {
            return null;
        }
        return arrayHandler.read1(_family, bytes);
    }
    
    public final Object readQuery(YapArray arrayHandler, Transaction trans, YapReader reader) throws CorruptionException{
        TypeHandler4 typeHandler = arrayHandler.i_handler;
        if(typeHandler.isSecondClass() == YapConst.YES){
            return readQueryEmbedded(arrayHandler, trans, reader);
        }else{
            return readQueryLinked(arrayHandler, trans, reader);
        }
    }
    
    public final Object readQueryLinked(YapArray arrayHandler, Transaction trans, YapReader reader) throws CorruptionException{
        YapReader bytes = reader.readEmbeddedObject(trans);
        if (bytes == null) {
            return null;
        }
        Object array = arrayHandler.read1Query(trans,_family, bytes);
        return array;
    }
    
    public final Object readQueryEmbedded(YapArray arrayHandler, Transaction trans, YapReader reader) throws CorruptionException{
        reader._offset = reader.readInt();
        return arrayHandler.read1Query(trans,_family, reader);
    }
    
    public Object writeNew(YapArray arrayHandler, Object a_object, YapWriter a_bytes) {
        if (a_object == null) {
            a_bytes.writeEmbeddedNull();
            return null;
        }
        TypeHandler4 typeHandler = arrayHandler.i_handler;
        if(typeHandler.isSecondClass() == YapConst.YES){
            writeNewEmbedded(arrayHandler, a_object, a_bytes);
        }else{
            writeNewLinked(arrayHandler, a_object, a_bytes);
        }
        return a_object;
    }
    
    private void writeNewEmbedded(YapArray arrayHandler, Object obj, YapWriter writer) {
        
        int length = arrayHandler.objectLength(obj);
        
        // TODO: could be writer.reserveAndPointToPayLoadSlot(length);
        
        writer.writeInt(writer._payloadOffset);
        writer.writeInt(length);
        int linkOffset = writer._offset;
        writer._offset = writer._payloadOffset;
        writer._payloadOffset += length;
        
        arrayHandler.writeNew1(obj, writer, length);
        
        writer._offset = linkOffset;
    }
    
    private void writeNewLinked(YapArray arrayHandler, Object a_object, YapWriter writer) {
        int length = arrayHandler.objectLength(a_object);
        YapWriter subWriter = new YapWriter(writer.getTransaction(), length);
        subWriter.setUpdateDepth(writer.getUpdateDepth());
        arrayHandler.writeNew1(a_object, subWriter, length);
        subWriter.setID(writer._offset);
        writer.getStream().writeEmbedded(writer, subWriter);
        writer.incrementOffset(YapConst.YAPID_LENGTH);
        writer.writeInt(length);
    }
    



}

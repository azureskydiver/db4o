/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;


class ArrayMarshaller1 extends ArrayMarshaller{
    
    private int calculateLength(Transaction trans, YapArray arrayHandler, Object obj){
        
        TypeHandler4 typeHandler = arrayHandler.i_handler;
        
        int length = arrayHandler.objectLength(obj);
        
        if(! typeHandler.hasFixedLength()){
            Object[] all = arrayHandler.allElements(obj);
            for (int i = 0; i < all.length; i++) {
                Object object = all[i];
                
                // FIXME: Actually we need the length for the links of the object
                // and the length in the payload here, so really the following
                // should be used:
                
                // length += typeHandler.linkLength();
                

                // ...but that looks like problems for 'ANY' types where
                // length in payload should get the 'false' parameter for
                // 'topLevel'
                
                // For now this is handled in YapClassAny by reacting to 
                // the return value from lengthInPayLoad
                
                length += typeHandler.lengthInPayload(trans, object, true);
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
    
    public int lengthInPayload(Transaction trans, YapArray arrayHandler, Object obj, boolean topLevel){
        
        TypeHandler4 typeHandler = arrayHandler.i_handler;
        
        if(typeHandler.isSecondClass() == YapConst.NO){
            
            if(! topLevel){
                return arrayHandler.linkLength();  
            }

            return 0;
        }
        
        if(typeHandler.isSecondClass() == YapConst.UNKNOWN){
            
            if(! topLevel){
                return arrayHandler.linkLength();  
            }
            
            return 0;
        }
        
        if(typeHandler.isSecondClass() == YapConst.YES){
            return calculateLength(trans, arrayHandler, obj);
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
    
    public void readCandidates(YapArray arrayHandler, YapReader reader, QCandidates candidates) {
        TypeHandler4 typeHandler = arrayHandler.i_handler;
        if(typeHandler.isSecondClass() == YapConst.YES){
            readEmbeddedCandidates(arrayHandler, reader, candidates);
        }else{
            readLinkedCandidates(arrayHandler, reader, candidates);
        }
    }
    
    public void readEmbeddedCandidates(YapArray arrayHandler, YapReader reader, QCandidates candidates){
        reader._offset = reader.readInt();
        arrayHandler.read1Candidates(_family, reader, candidates);
    }
    
    public void readLinkedCandidates(YapArray arrayHandler, YapReader reader, QCandidates candidates){
        YapReader subReader = reader.readEmbeddedObject(candidates.i_trans);
        if (subReader == null) {
            return;
        }
        arrayHandler.read1Candidates(_family, subReader, candidates);
    }
    
    private Object readEmbedded(YapArray arrayHandler, YapWriter reader) throws CorruptionException{
        int linkOffSet = reader.preparePayloadRead();
        Object array = arrayHandler.read1(_family, reader);
        reader._offset = linkOffSet;
        return array;
    }
    
    private Object readLinked(YapArray arrayHandler, YapWriter reader) throws CorruptionException{
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
    
    private void writeNewLinked(YapArray arrayHandler, Object obj, YapWriter writer) {
        Transaction trans = writer.getTransaction();
        int length = calculateLength(trans, arrayHandler, obj);
        YapWriter subWriter = new YapWriter(trans, length);
        subWriter._payloadOffset = arrayHandler.objectLength(obj);
        subWriter.setUpdateDepth(writer.getUpdateDepth());
        arrayHandler.writeNew1(obj, subWriter, length);
        subWriter.setID(writer._offset);
        writer.getStream().writeEmbedded(writer, subWriter);
        writer.incrementOffset(YapConst.YAPID_LENGTH);
        writer.writeInt(length);
    }
    



}

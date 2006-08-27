/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;


class ArrayMarshaller1 extends ArrayMarshaller{
    
    public void calculateLengths(Transaction trans, ObjectHeaderAttributes header, YapArray arrayHandler, Object obj, boolean topLevel){
        
        TypeHandler4 typeHandler = arrayHandler.i_handler;
        
        if(topLevel){
            header.addBaseLength(arrayHandler.linkLength());
        } else{
            header.addPayLoadLength(arrayHandler.linkLength());
        }

        if(typeHandler.hasFixedLength()){
            header.addPayLoadLength(arrayHandler.objectLength(obj));
        }else{
            header.addPayLoadLength(arrayHandler.ownLength(obj));
            Object[] all = arrayHandler.allElements(obj);
            for (int i = 0; i < all.length; i++) {
                typeHandler.calculateLengths(trans, header, false, all[i], true);
            }
        }
    }
    
    public TreeInt collectIDs(YapArray arrayHandler, TreeInt tree, YapWriter reader){
        reader._offset = reader.readInt();
        return arrayHandler.collectIDs1(reader.getTransaction(), tree, reader);
    }
    
    public void deleteEmbedded(YapArray arrayHandler, YapWriter reader) {
        
        int address = reader.readInt();
        reader.readInt();  // length
        if (address <= 0) {
            return;
        }
        
        int linkOffSet = reader._offset; 
        
        Transaction trans = reader.getTransaction();
        TypeHandler4 typeHandler = arrayHandler.i_handler;
        
        if (reader.cascadeDeletes() > 0 && typeHandler instanceof YapClass) {
            reader._offset = address;
            if (Deploy.debug) {
                reader.readBegin(arrayHandler.identifier());
            }
            reader.setCascadeDeletes(reader.cascadeDeletes());
            for (int i = arrayHandler.elementCount(trans, reader); i > 0; i--) {
                arrayHandler.i_handler.deleteEmbedded(_family, reader);
            }
        }
        
        if(linkOffSet > 0){
            reader._offset = linkOffSet;
        }
    }
    
    public Object read(YapArray arrayHandler,  YapWriter reader) throws CorruptionException{
        int linkOffSet = reader.preparePayloadRead();
        Object array = arrayHandler.read1(_family, reader);
        reader._offset = linkOffSet;
        return array;
    }
    
    public void readCandidates(YapArray arrayHandler, YapReader reader, QCandidates candidates) {
        reader._offset = reader.readInt();
        arrayHandler.read1Candidates(_family, reader, candidates);
    }
    
    public final Object readQuery(YapArray arrayHandler, Transaction trans, YapReader reader) throws CorruptionException{
        reader._offset = reader.readInt();
        return arrayHandler.read1Query(trans,_family, reader);
    }
    
    public Object writeNew(YapArray arrayHandler, Object obj, boolean restoreLinkOffset, YapWriter writer) {
        if (obj == null) {
            writer.writeEmbeddedNull();
            return null;
        }
        int length = arrayHandler.objectLength(obj);
        int linkOffset = writer.reserveAndPointToPayLoadSlot(length);
        arrayHandler.writeNew1(obj, writer, length);
        if(restoreLinkOffset){
            writer._offset = linkOffset;
        }
        return obj;
    }
    



}

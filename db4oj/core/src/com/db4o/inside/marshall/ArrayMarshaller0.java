/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;


class ArrayMarshaller0  extends ArrayMarshaller{
    
    public TreeInt collectIDs(YapArray arrayHandler, TreeInt tree, YapWriter reader){
        Transaction trans = reader.getTransaction();
        return arrayHandler.collectIDs1(trans, tree, reader.readEmbeddedObject(trans));
    }
    
    public void deleteEmbedded(YapArray arrayHandler, YapWriter reader) {
        int address = reader.readInt();
        int length = reader.readInt();
        if (address <= 0) {
            return;
        }
        Transaction trans = reader.getTransaction();
        if (reader.cascadeDeletes() > 0 && arrayHandler.i_handler instanceof YapClass) {
            YapWriter bytes =
                reader.getStream().readObjectWriterByAddress(
                    trans,
                    address,
                    length);
            if (bytes != null) {
                if (Deploy.debug) {
                    bytes.readBegin(arrayHandler.identifier());
                }
                bytes.setCascadeDeletes(reader.cascadeDeletes());
                for (int i = arrayHandler.elementCount(trans, bytes); i > 0; i--) {
                    arrayHandler.i_handler.deleteEmbedded(_family, bytes);
                }
            }
        }
        trans.slotFreeOnCommit(address, address, length);
    }
    
    public void calculateLengths(Transaction trans, ObjectHeaderAttributes header, YapArray handler, Object obj, boolean topLevel){
        // do nothing
    }
    
    public Object writeNew(YapArray arrayHandler, Object a_object, boolean topLevel, YapWriter a_bytes) {
        if (a_object == null) {
            a_bytes.writeEmbeddedNull();
            return null;
        }
        int length = arrayHandler.objectLength(a_object);
        YapWriter bytes = new YapWriter(a_bytes.getTransaction(), length);
        bytes.setUpdateDepth(a_bytes.getUpdateDepth());
        arrayHandler.writeNew1(a_object, bytes, length);
        bytes.setID(a_bytes._offset);
        a_bytes.getStream().writeEmbedded(a_bytes, bytes);
        a_bytes.incrementOffset(YapConst.ID_LENGTH);
        a_bytes.writeInt(length);
        return a_object;
    }
    
    public Object read(YapArray arrayHandler,  YapWriter a_bytes) throws CorruptionException{
        YapWriter bytes = a_bytes.readEmbeddedObject();
        if (bytes == null) {
            return null;
        }
        return arrayHandler.read1(_family, bytes);
    }
    
    public void readCandidates(YapArray arrayHandler, YapReader reader, QCandidates candidates) {
        YapReader bytes = reader.readEmbeddedObject(candidates.i_trans);
        if (bytes == null) {
            return;
        }
        if(Deploy.debug){
            bytes.readBegin(arrayHandler.identifier());
        }
        int count = arrayHandler.elementCount(candidates.i_trans, bytes);
        for (int i = 0; i < count; i++) {
            candidates.addByIdentity(new QCandidate(candidates, null, bytes.readInt(), true));
        }
    }

    
    public final Object readQuery(YapArray arrayHandler, Transaction trans, YapReader reader) throws CorruptionException{
        YapReader bytes = reader.readEmbeddedObject(trans);
        if (bytes == null) {
            return null;
        }
        Object array = arrayHandler.read1Query(trans,_family, bytes);
        return array;
    }
    

    

}

/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;


public class StringMarshaller0 extends StringMarshaller {
    
    public boolean inlinedStrings(){
        return false;
    }
    
    public void calculateLengths(Transaction trans, ObjectHeaderAttributes header, boolean topLevel, Object obj, boolean withIndirection) {
        // do nothing
    }
    
    public Object writeNew(Object a_object, boolean topLevel, YapWriter a_bytes, boolean redirect) {
        if (a_object == null) {
            a_bytes.writeEmbeddedNull();
            return null;
        }
        
        YapStream stream = a_bytes.getStream();
        String str = (String) a_object;
        int length = stream.stringIO().length(str);
        
        YapWriter bytes = new YapWriter(a_bytes.getTransaction(), length);
        
        writeShort(stream, str, bytes);
        
        bytes.setID(a_bytes._offset);
        
        a_bytes.getStream().writeEmbedded(a_bytes, bytes);
        a_bytes.incrementOffset(YapConst.ID_LENGTH);
        a_bytes.writeInt(length);
        return bytes;
    }
    
    public YapReader readIndexEntry(YapWriter parentSlot) throws CorruptionException{
        return parentSlot.getStream().readWriterByAddress(parentSlot.getTransaction(), parentSlot.readInt(), parentSlot.readInt());
    }
    
    public YapReader readSlotFromParentSlot(YapStream stream, YapReader reader) throws CorruptionException {
        return reader.readEmbeddedObject(stream.getTransaction());
    }

	public void defrag(SlotReader reader) {
	}
}

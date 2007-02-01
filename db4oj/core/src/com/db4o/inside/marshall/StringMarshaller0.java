/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;
import com.db4o.inside.*;


public class StringMarshaller0 extends StringMarshaller {
    
    public boolean inlinedStrings(){
        return false;
    }
    
    public void calculateLengths(Transaction trans, ObjectHeaderAttributes header, boolean topLevel, Object obj, boolean withIndirection) {
        // do nothing
    }
    
    public Object writeNew(Object a_object, boolean topLevel, StatefulBuffer a_bytes, boolean redirect) {
        if (a_object == null) {
            a_bytes.writeEmbeddedNull();
            return null;
        }
        
        ObjectContainerBase stream = a_bytes.getStream();
        String str = (String) a_object;
        int length = stream.stringIO().length(str);
        
        StatefulBuffer bytes = new StatefulBuffer(a_bytes.getTransaction(), length);
        
        writeShort(stream, str, bytes);
        
        bytes.setID(a_bytes._offset);
        
        a_bytes.getStream().writeEmbedded(a_bytes, bytes);
        a_bytes.incrementOffset(YapConst.ID_LENGTH);
        a_bytes.writeInt(length);
        return bytes;
    }
    
    public Buffer readIndexEntry(StatefulBuffer parentSlot) throws CorruptionException{
        return parentSlot.getStream().readWriterByAddress(parentSlot.getTransaction(), parentSlot.readInt(), parentSlot.readInt());
    }
    
    public Buffer readSlotFromParentSlot(ObjectContainerBase stream, Buffer reader) throws CorruptionException {
        return reader.readEmbeddedObject(stream.getTransaction());
    }

	public void defrag(SlotReader reader) {
	}
}

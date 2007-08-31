/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.internal.*;


public class StringMarshaller0 extends StringMarshaller {
    
    public boolean inlinedStrings(){
        return false;
    }
    
    
    public Buffer readIndexEntry(StatefulBuffer parentSlot) throws CorruptionException, Db4oIOException{
        return parentSlot.getStream().readWriterByAddress(parentSlot.getTransaction(), parentSlot.readInt(), parentSlot.readInt());
    }
    
    public Buffer readSlotFromParentSlot(ObjectContainerBase stream, Buffer reader) throws CorruptionException, Db4oIOException {
        return reader.readEmbeddedObject(stream.transaction());
    }

	public void defrag(SlotBuffer reader) {
	}
	
}

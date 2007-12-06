/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.slots.*;


class ArrayMarshaller0  extends ArrayMarshaller{
    
    public void deleteEmbedded(ArrayHandler arrayHandler, StatefulBuffer parentBuffer) throws Db4oIOException {
    	Slot slot = parentBuffer.readSlot();
        if (slot.address()<= 0) {
            return;
        }
        Transaction trans = parentBuffer.getTransaction();
        if (parentBuffer.cascadeDeletes() > 0
				&& arrayHandler._handler instanceof ClassMetadata) {
			StatefulBuffer arrayBuffer = parentBuffer.getStream().readWriterByAddress(
					trans, slot.address(), slot.length());
			if (Deploy.debug) {
				arrayBuffer.readBegin(arrayHandler.identifier());
			}
			DeleteContext deleteContext = new DeleteContext(_family, arrayBuffer);
			arrayBuffer.setCascadeDeletes(parentBuffer.cascadeDeletes());
			for (int i = arrayHandler.elementCount(trans, arrayBuffer); i > 0; i--) {
				arrayHandler._handler.delete(deleteContext);
			}
		}
        trans.slotFreeOnCommit(slot.address(), slot);
    }
    
    protected Buffer prepareIDReader(Transaction trans,Buffer reader) throws Db4oIOException {
    	return reader.readEmbeddedObject(trans);
    }
    
    public void defragIDs(ArrayHandler arrayHandler,BufferPair readers) {
    	// Where is the code ???
    }
}

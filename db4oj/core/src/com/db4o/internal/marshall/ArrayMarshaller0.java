/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.slots.*;


class ArrayMarshaller0  extends ArrayMarshaller{
    
    public void deleteEmbedded(ArrayHandler arrayHandler, StatefulBuffer reader) throws Db4oIOException {
    	Slot slot = reader.readSlot();
        if (slot.address()<= 0) {
            return;
        }
        Transaction trans = reader.getTransaction();
        if (reader.cascadeDeletes() > 0
				&& arrayHandler._handler instanceof ClassMetadata) {
			StatefulBuffer bytes = reader.getStream().readWriterByAddress(
					trans, slot.address(), slot.length());
			if (Deploy.debug) {
				bytes.readBegin(arrayHandler.identifier());
			}
			bytes.setCascadeDeletes(reader.cascadeDeletes());
			for (int i = arrayHandler.elementCount(trans, bytes); i > 0; i--) {
				arrayHandler._handler.deleteEmbedded(_family, bytes);
			}
		}
        trans.slotFreeOnCommit(slot.address(), slot);
    }
    
    public Object read(ArrayHandler arrayHandler,  StatefulBuffer a_bytes) throws CorruptionException, Db4oIOException {
        StatefulBuffer bytes = a_bytes.readEmbeddedObject();
        if(bytes == null){
            return null;
        }
        return arrayHandler.read1(_family, bytes);
    }
    
    protected Buffer prepareIDReader(Transaction trans,Buffer reader) throws Db4oIOException {
    	return reader.readEmbeddedObject(trans);
    }
    
    public void defragIDs(ArrayHandler arrayHandler,BufferPair readers) {
    }
}

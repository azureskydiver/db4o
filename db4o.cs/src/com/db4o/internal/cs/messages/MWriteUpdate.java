/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.*;
import com.db4o.internal.slots.*;

public final class MWriteUpdate extends MsgObject implements ServerSideMessage {
	
	public final boolean processAtServer() {
	    int yapClassId = _payLoad.readInt();
	    LocalObjectContainer stream = (LocalObjectContainer)stream();
	    unmarshall(_payLoad._offset);
	    synchronized(streamLock()){
	        ClassMetadata yc = stream.classMetadataForId(yapClassId);
			int id = _payLoad.getID();
			transaction().dontDelete(id);
            Slot oldSlot = ((LocalTransaction)transaction()).getCommittedSlotOfID(id);
            stream.getSlotForUpdate(_payLoad);
			yc.addFieldIndices(_payLoad, oldSlot);
            _payLoad.writeEncrypt();
		}
		return true;
	}
}
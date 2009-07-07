/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.cs.internal.messages;

import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.slots.*;

public final class MWriteUpdate extends MsgObject implements ServerSideMessage {
	
	public final boolean processAtServer() {
	    int classMetadataID = _payLoad.readInt();
	    int arrayTypeValue = _payLoad.readInt();
	    ArrayType arrayType = ArrayType.forValue(arrayTypeValue);
	    LocalObjectContainer stream = (LocalObjectContainer)stream();
	    unmarshall(_payLoad._offset);
	    synchronized(streamLock()){
	        ClassMetadata classMetadata = stream.classMetadataForID(classMetadataID);
			int id = _payLoad.getID();
			transaction().writeUpdateAdjustIndexes(id, classMetadata, arrayType, 0);
			transaction().dontDelete(id);
            Slot oldSlot = ((LocalTransaction)transaction()).getCommittedSlotOfID(id);
            stream.getSlotForUpdate(_payLoad);
			classMetadata.addFieldIndices(_payLoad, oldSlot);
            _payLoad.writeEncrypt();
            deactivateCacheFor(id);            
		}
		return true;
	}

	private void deactivateCacheFor(int id) {
		transaction().deactivate(id, new FixedActivationDepth(1));
	}
}
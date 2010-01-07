/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.cs.internal.messages;

import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.slots.*;

public final class MWriteUpdate extends MsgObject implements ServerSideMessage {
	
	public final void processAtServer() {
	    int classMetadataID = _payLoad.readInt();
	    int arrayTypeValue = _payLoad.readInt();
	    ArrayType arrayType = ArrayType.forValue(arrayTypeValue);
	    unmarshall(_payLoad._offset);
	    synchronized(containerLock()){
	        ClassMetadata classMetadata = localContainer().classMetadataForID(classMetadataID);
			int id = _payLoad.getID();
			transaction().writeUpdateAdjustIndexes(id, classMetadata, arrayType, 0);
			transaction().dontDelete(id);
			Slot oldSlot = localContainer().idSystem().getCommittedSlotOfID(serverTransaction(), id);
            localContainer().getSlotForUpdate(_payLoad);
			classMetadata.addFieldIndices(_payLoad, oldSlot);
            _payLoad.writeEncrypt();
            deactivateCacheFor(id);            
		}
	}

	private void deactivateCacheFor(int id) {
		ObjectReference reference = transaction().referenceForId(id);
		if (null == reference) {
			return;
		}
		reference.deactivate(transaction(), new FixedActivationDepth(1));
	}
}
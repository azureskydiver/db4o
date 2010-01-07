/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.cs.internal.messages;

import com.db4o.cs.internal.*;
import com.db4o.internal.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;

public final class MWriteNew extends MsgObject implements ServerSideMessage {
	
	public final void processAtServer() {
        int classMetadataId = _payLoad.readInt();
        unmarshall(_payLoad._offset);
        synchronized (containerLock()) {
            ClassMetadata classMetadata = classMetadataId == 0
            					? null
            					: localContainer().classMetadataForID(classMetadataId);
            
            int id = _payLoad.getID();
            prefetchedIDConsumed(id);            
            IdSystem idSystem = localContainer().idSystem();
			idSystem.slotFreePointerOnRollback(transaction(), id);
            
            Slot slot = localContainer().getSlot(_payLoad.length());
            _payLoad.address(slot.address());
            
            idSystem.slotFreeOnRollback(transaction(), id, slot);
            
            if(classMetadata != null){
                classMetadata.addFieldIndices(_payLoad,null);
            }
            localContainer().writeNew(transaction(), _payLoad.pointer(), classMetadata, _payLoad);
            idSystem.setPointer(transaction(), id, slot);
        }
    }

	private void prefetchedIDConsumed(int id) {
		ServerMessageDispatcherImpl serverMessageDispatcher = (ServerMessageDispatcherImpl) serverMessageDispatcher();
		serverMessageDispatcher.prefetchedIDConsumed(id);
	}
}
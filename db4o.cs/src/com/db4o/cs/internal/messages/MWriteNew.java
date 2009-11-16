/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.cs.internal.messages;

import com.db4o.internal.*;
import com.db4o.internal.slots.*;

public final class MWriteNew extends MsgObject implements ServerSideMessage {
	
	public final void processAtServer() {
        int classMetadataId = _payLoad.readInt();
        LocalObjectContainer container = (LocalObjectContainer)stream();
        unmarshall(_payLoad._offset);
        synchronized (streamLock()) {
            ClassMetadata classMetadata = classMetadataId == 0
            					? null
            					: container.classMetadataForID(classMetadataId);
            
            int id = _payLoad.getID();
            container.prefetchedIDConsumed(id);
            transaction().slotFreePointerOnRollback(id);
            
            Slot slot = container.getSlot(_payLoad.length());
            _payLoad.address(slot.address());
            
            transaction().slotFreeOnRollback(id, slot);
            
            if(classMetadata != null){
                classMetadata.addFieldIndices(_payLoad,null);
            }
            container.writeNew(transaction(), _payLoad.pointer(), classMetadata, _payLoad);
            transaction().setPointer(id, slot);
        }
    }
}
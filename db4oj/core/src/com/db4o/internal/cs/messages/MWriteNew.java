/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.*;
import com.db4o.internal.slots.*;

public final class MWriteNew extends MsgObject implements ServerSideMessage {
	
	public final boolean processAtServer() {
        int yapClassId = _payLoad.readInt();
        LocalObjectContainer stream = (LocalObjectContainer)stream();
        unmarshall(_payLoad._offset);
        synchronized (streamLock()) {
            ClassMetadata yc = yapClassId == 0 ? null : stream.classMetadataForId(yapClassId);
            _payLoad.writeEmbedded();
            
            int id = _payLoad.getID();
            stream.prefetchedIDConsumed(id);
            transaction().slotFreePointerOnRollback(id);
            
            Slot slot = stream.getSlot(_payLoad.getLength());
            _payLoad.address(slot.address());
            
            transaction().slotFreeOnRollback(id, slot.address(), slot.length());
            
            if(yc != null){
                yc.addFieldIndices(_payLoad,null);
            }
            stream.writeNew(yc, _payLoad);
            serverTransaction().writePointer( id, slot.address(), slot.length());
        }
        return true;
    }
}
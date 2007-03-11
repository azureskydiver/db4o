/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.*;
import com.db4o.internal.cs.*;

public final class MWriteNew extends MsgObject {
	
	public final boolean processAtServer(ServerMessageDispatcher serverThread) {
        int yapClassId = _payLoad.readInt();
        LocalObjectContainer stream = (LocalObjectContainer)stream();
        unmarshall(_payLoad._offset);
        synchronized (streamLock()) {
            ClassMetadata yc = yapClassId == 0 ? null : stream.getYapClass(yapClassId);
            _payLoad.writeEmbedded();
            
            int id = _payLoad.getID();
            int length = _payLoad.getLength();
            
            stream.prefetchedIDConsumed(id);
            transaction().slotFreePointerOnRollback(id);
            
            int address = stream.getSlot(length);
            _payLoad.address(address);
            
            transaction().slotFreeOnRollback(id, address, length);
            
            if(yc != null){
                yc.addFieldIndices(_payLoad,null);
            }
            stream.writeNew(yc, _payLoad);
            transaction().writePointer( id, address, length);
        }
        return true;
    }
}
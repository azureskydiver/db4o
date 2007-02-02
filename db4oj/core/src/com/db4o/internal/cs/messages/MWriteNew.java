/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.*;
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
            stream.prefetchedIDConsumed(_payLoad.getID());
            _payLoad.address(stream.getSlot(_payLoad.getLength()));
            if(yc != null){
                yc.addFieldIndices(_payLoad,null);
            }
            stream.writeNew(yc, _payLoad);
            transaction().writePointer(
                _payLoad.getID(),
                _payLoad.getAddress(),
                _payLoad.getLength());
        }
        return true;
    }
}
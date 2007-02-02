/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.*;
import com.db4o.internal.slots.*;

public final class MWriteUpdate extends MsgObject {
	
	public final boolean processAtServer(ServerMessageDispatcher serverThread) {
	    int yapClassId = _payLoad.readInt();
	    LocalObjectContainer stream = (LocalObjectContainer)stream();
	    unmarshall(_payLoad._offset);
	    synchronized(streamLock()){
	        ClassMetadata yc = stream.getYapClass(yapClassId);
			_payLoad.writeEmbedded();
			int id = _payLoad.getID();
			transaction().dontDelete(id);
            Slot oldSlot = ((LocalTransaction)_trans).getCommittedSlotOfID(id);
            stream.getSlotForUpdate(_payLoad);
			yc.addFieldIndices(_payLoad, oldSlot);
            _payLoad.writeEncrypt();
		}
		return true;
	}
}
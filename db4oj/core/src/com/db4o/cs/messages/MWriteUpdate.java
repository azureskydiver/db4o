/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.inside.slots.*;

public final class MWriteUpdate extends MsgObject {
	
	public final boolean processAtServer(YapServerThread serverThread) {
	    int yapClassId = _payLoad.readInt();
	    YapFile stream = (YapFile)stream();
	    unmarshall(YapConst.INT_LENGTH);
	    synchronized(streamLock()){
	        YapClass yc = stream.getYapClass(yapClassId);
			_payLoad.writeEmbedded();
			int id = _payLoad.getID();
			transaction().dontDelete(id);
            Slot oldSlot = _trans.getCommittedSlotOfID(id);
            stream.getSlotForUpdate(_payLoad);
			yc.addFieldIndices(_payLoad, oldSlot);
            _payLoad.writeEncrypt();
		}
		return true;
	}
}
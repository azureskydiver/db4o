/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.inside.slots.*;

public final class MWriteUpdate extends MsgObject {
	public final boolean processAtServer(YapServerThread serverThread) {
	    int yapClassId = _payLoad.readInt();
	    YapFile stream = (YapFile)getStream();
	    unmarshall(YapConst.INT_LENGTH);
	    synchronized(stream.i_lock){
	        YapClass yc = stream.getYapClass(yapClassId);
			_payLoad.writeEmbedded();
            Slot oldSlot = _trans.getCommittedSlotOfID(_payLoad.getID());
            stream.getSlotForUpdate(_payLoad);
			yc.addFieldIndices(_payLoad, oldSlot);
            _payLoad.writeEncrypt();
		}
		return true;
	}
}
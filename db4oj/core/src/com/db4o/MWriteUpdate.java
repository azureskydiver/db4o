/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.YapSocket;
import com.db4o.inside.slots.*;

final class MWriteUpdate extends MsgObject {
	final boolean processMessageAtServer(YapSocket sock) {
	    int yapClassId = _payLoad.readInt();
	    YapFile stream = (YapFile)getStream();
	    unmarshall(YapConst.INT_LENGTH);
	    synchronized(stream.i_lock){
	        YapClass yc = stream.getYapClass(yapClassId);
			_payLoad.writeEmbedded();
            Slot oldSlot = _trans.getSlotInformation(_payLoad.getID());
            stream.getSlotForUpdate(_payLoad);
			yc.addFieldIndices(_payLoad, oldSlot);
            stream.i_handlers.encrypt(_payLoad);
            _payLoad.write();
		}
		return true;
	}
}
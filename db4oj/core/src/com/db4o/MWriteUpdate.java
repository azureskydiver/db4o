/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.YapSocket;

final class MWriteUpdate extends MsgObject {
	final boolean processMessageAtServer(YapSocket sock) {
	    int yapClassId = _payLoad.readInt();
	    YapFile stream = (YapFile)getStream();
	    unmarshall(YapConst.YAPINT_LENGTH);
	    synchronized(stream.i_lock){
	        YapClass yc = stream.getYapClass(yapClassId);
			_payLoad.writeEmbedded();
			yc.addFieldIndices(_payLoad, false);
			stream.writeUpdate(yc, _payLoad);
		}
		return true;
	}
}
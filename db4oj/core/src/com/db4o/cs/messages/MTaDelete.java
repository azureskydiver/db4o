/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.foundation.network.YapSocket;

/**
 * 
 */
public class MTaDelete extends MsgD {
	public final boolean processMessageAtServer(YapSocket in) {
	    int id = _payLoad.readInt();
	    int cascade = _payLoad.readInt();
	    Transaction trans = getTransaction();
	    YapStream stream = trans.stream();
	    synchronized (stream.i_lock) {
	        Object[] arr = stream.getObjectAndYapObjectByID(trans, id);
	        trans.delete((YapObject)arr[1], cascade);
	        return true;
	    }
	}
}

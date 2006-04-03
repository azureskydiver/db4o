/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.YapSocket;

/**
 * 
 */
class MTaDelete extends MsgD {
	public MTaDelete() {
		super();
	}

	public MTaDelete(MsgCloneMarker marker) {
		super(marker);
	}

    
	final boolean processMessageAtServer(YapSocket in) {
	    int id = _payLoad.readInt();
	    int cascade = _payLoad.readInt();
	    Transaction trans = getTransaction();
	    YapStream stream = trans.i_stream;
	    synchronized (stream.i_lock) {
	        Object[] arr = stream.getObjectAndYapObjectByID(trans, id);
	        trans.delete((YapObject)arr[1], cascade);
	        return true;
	    }
	}

	public Object shallowClone() {
		return shallowCloneInternal(new MTaDelete(MsgCloneMarker.INSTANCE));
	}
}

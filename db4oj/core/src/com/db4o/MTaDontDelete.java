/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.YapSocket;


/**
 * 
 */
class MTaDontDelete extends MsgD {
	public MTaDontDelete() {
		super();
	}

	public MTaDontDelete(MsgCloneMarker marker) {
		super(marker);
	}
 
	final boolean processMessageAtServer(YapSocket in) {
        int classID = _payLoad.readInt();
	    int id = _payLoad.readInt();
	    Transaction trans = getTransaction();
	    YapStream stream = trans.i_stream;
	    synchronized (stream.i_lock) {
	        trans.dontDelete(classID, id);
	        return true;
	    }
	}
	
	public Object shallowClone() {
		return shallowCloneInternal(new MTaDontDelete(MsgCloneMarker.INSTANCE));
	}
}

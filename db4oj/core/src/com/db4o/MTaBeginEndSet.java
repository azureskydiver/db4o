/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.YapSocket;


/**
 * 
 */
class MTaBeginEndSet extends Msg {
	final boolean processMessageAtServer(YapSocket in) {
	    synchronized (getStream().i_lock) {
	        getTransaction().beginEndSet();
	        return true;
	    }
	}
}

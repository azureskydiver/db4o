/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;


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

/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.cs.*;


/**
 * 
 */
public class MTaBeginEndSet extends Msg {
	public final boolean processAtServer(YapServerThread serverThread) {
	    synchronized (getStream().i_lock) {
	        getTransaction().beginEndSet();
	        return true;
	    }
	}
}

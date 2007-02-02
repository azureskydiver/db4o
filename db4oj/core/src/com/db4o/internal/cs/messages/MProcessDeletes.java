/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.cs.*;


public class MProcessDeletes extends Msg {
	
	public final boolean processAtServer(ServerMessageDispatcher serverThread) {
	    synchronized (streamLock()) {
	        transaction().processDeletes();
	        return true;
	    }
	}
	
}

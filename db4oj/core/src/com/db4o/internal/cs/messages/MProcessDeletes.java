/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;



public class MProcessDeletes extends Msg implements ServerSideMessage {
	
	public final boolean processAtServer() {
	    synchronized (streamLock()) {
	        transaction().processDeletes();
	        return true;
	    }
	}
	
}

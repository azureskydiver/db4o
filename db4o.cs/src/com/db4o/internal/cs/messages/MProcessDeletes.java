/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.*;
import com.db4o.ext.*;



public class MProcessDeletes extends Msg implements ServerSideMessage {
	
	public final boolean processAtServer() {
	    synchronized (streamLock()) {
			try {
				transaction().processDeletes();
			} catch (Db4oException e) {
				// Don't send the exception to the user because delete is asynchronous
				if(Debug4.atHome){
					e.printStackTrace();
				}
			}
		}
		return true;
	}
	
}

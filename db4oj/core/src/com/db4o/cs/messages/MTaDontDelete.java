/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.cs.*;

public class MTaDontDelete extends MsgD {
	
	public final boolean processAtServer(YapServerThread serverThread) {
        int classID = _payLoad.readInt();
	    int id = _payLoad.readInt();
	    synchronized (streamLock()) {
	        transaction().dontDelete(classID, id);
	        return true;
	    }
	}
}

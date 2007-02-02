/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.inside.*;

public class MTaDelete extends MsgD {
	
	public final boolean processAtServer(ServerMessageDispatcher serverThread) {
	    int id = _payLoad.readInt();
	    int cascade = _payLoad.readInt();
	    Transaction trans = transaction();
	    synchronized (streamLock()) {
	        trans.delete(null, id, cascade);
	        return true;
	    }
	}
}

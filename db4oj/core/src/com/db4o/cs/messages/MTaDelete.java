/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.cs.*;

public class MTaDelete extends MsgD {
	
	public final boolean processAtServer(YapServerThread serverThread) {
	    int id = _payLoad.readInt();
	    int cascade = _payLoad.readInt();
	    Transaction trans = transaction();
	    synchronized (streamLock()) {
	        trans.delete(null, id, cascade);
	        return true;
	    }
	}
}

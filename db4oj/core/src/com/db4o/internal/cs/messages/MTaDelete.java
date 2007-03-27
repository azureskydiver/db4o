/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.*;

public class MTaDelete extends MsgD implements ServerSideMessage {
	
	public final boolean processAtServer() {
	    int id = _payLoad.readInt();
	    int cascade = _payLoad.readInt();
	    Transaction trans = transaction();
	    synchronized (streamLock()) {
	        trans.delete(null, id, cascade);
	        return true;
	    }
	}
}

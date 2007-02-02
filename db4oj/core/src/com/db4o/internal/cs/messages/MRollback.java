/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.cs.*;

public final class MRollback extends Msg {
	
	public final boolean processAtServer(ServerMessageDispatcher serverThread) {
		transaction().rollback();
		return true;
	}
	
}
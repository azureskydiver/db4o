/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.inside.*;

public final class MReleaseSemaphore extends MsgD {
	public final boolean processAtServer(ServerMessageDispatcher serverThread) {
		String name = readString();
		((LocalObjectContainer)stream()).releaseSemaphore(transaction(),name);
		return true;
	}
}
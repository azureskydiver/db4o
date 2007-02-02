/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.*;
import com.db4o.internal.cs.*;

public final class MReleaseSemaphore extends MsgD {
	public final boolean processAtServer(ServerMessageDispatcher serverThread) {
		String name = readString();
		((LocalObjectContainer)stream()).releaseSemaphore(transaction(),name);
		return true;
	}
}
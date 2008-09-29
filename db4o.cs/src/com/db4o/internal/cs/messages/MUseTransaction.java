/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.cs.*;


public final class MUseTransaction extends MsgD implements ServerSideMessage {

	public boolean processAtServer() {
		ServerMessageDispatcher serverThread = serverMessageDispatcher();
		serverThread.useTransaction(this);
		return true;
	}
}
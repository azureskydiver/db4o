/* Copyright (C) 2007  Versant Inc.  http://www.db4o.com */

package com.db4o.cs.internal.messages;

import com.db4o.cs.internal.*;


/**
 * @exclude
 */
public class MSwitchToFile extends MsgD implements ServerSideMessage {

	public boolean processAtServer() {
		ServerMessageDispatcher serverThread = serverMessageDispatcher();
		serverThread.switchToFile(this);
		return true;
	}
}

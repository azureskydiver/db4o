/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.cs.*;


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

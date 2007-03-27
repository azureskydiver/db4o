/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;


/**
 * @exclude
 */
public class MGetThreadID extends Msg implements ServerSideMessage {

	public boolean processAtServer() {
		respondInt(serverMessageDispatcher().dispatcherID());
		return true;
	}
}

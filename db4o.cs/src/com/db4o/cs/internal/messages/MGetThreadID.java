/* Copyright (C) 2007  Versant Inc.  http://www.db4o.com */

package com.db4o.cs.internal.messages;


/**
 * @exclude
 */
public class MGetThreadID extends Msg implements MessageWithResponse {

	public boolean processAtServer() {
		respondInt(serverMessageDispatcher().dispatcherID());
		return true;
	}
}
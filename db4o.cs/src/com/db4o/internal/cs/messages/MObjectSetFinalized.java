/* Copyright (C) 2007  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;



/**
 * @exclude
 */
public class MObjectSetFinalized extends MsgD implements ServerSideMessage {
	public boolean processAtServer() {
		int queryResultID = readInt();
    	serverMessageDispatcher().queryResultFinalized(queryResultID);
    	return true;
    }
}

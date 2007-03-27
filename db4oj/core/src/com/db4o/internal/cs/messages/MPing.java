/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;


/**
 * @exclude
 */
public class MPing extends Msg implements ServerSideMessage, ClientSideMessage {

	public boolean processAtServer() {
		write(Msg.OK);
		return true;
	}
	
	public boolean processAtClient() {
		write(Msg.OK);
		return true;
	}
}

/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;


/**
 * @exclude
 */
public class MPong extends Msg implements ServerSideMessage, ClientSideMessage {

	public boolean processAtServer() {
		return true;
	}

	public boolean processAtClient() {
		return true;
	}
	
}

/* Copyright (C) 2009   Versant Inc.   http://www.db4o.com */

package com.db4o.cs.internal.messages;


public class MRequestExceptionWithoutResponse extends MsgD implements ServerSideMessage {
	
	public void processAtServer() {
		throw ((RuntimeException)readSingleObject());
	}

}

/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.cs.*;


/**
 * @exclude
 */
public class MPing extends Msg implements ServerSideMessage, ClientSideMessage {

	public boolean processAtServer() {
		write(Msg.PONG);
		return true;
	}
	
	public boolean processAtClient() {
	    ((ClientObjectContainer)stream()).writeMessageToSocket(Msg.PONG);
		return true;
	}
}

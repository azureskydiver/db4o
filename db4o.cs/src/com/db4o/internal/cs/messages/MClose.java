/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;


/**
 * @exclude
 */
public class MClose extends Msg implements ServerSideMessage, ClientSideMessage {
	
	public boolean processAtServer() {
		synchronized (stream().lock()) {
			if (stream().isClosed()) {
				return true;
			}
			transaction().commit();
			logMsg(35, serverMessageDispatcher().name());
			serverMessageDispatcher().close();
		}
		return true;
	}
	
	public boolean processAtClient() {
		clientMessageDispatcher().close();
        return true; 
	}
}

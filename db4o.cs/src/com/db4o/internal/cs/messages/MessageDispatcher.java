/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;



/**
 * @exclude
 */
public interface MessageDispatcher {

	public boolean isMessageDispatcherAlive();
	
	public boolean write(Msg msg);
	
	public boolean close();

	public void setDispatcherName(String name);

	public void startDispatcher();
}

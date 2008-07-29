/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;



/**
 * @exclude
 */
public class MCommitSystemTransaction extends Msg implements ServerSideMessage {
	
	public final boolean processAtServer() {
		synchronized (streamLock()) {
			transaction().systemTransaction().commit();
		}
		return true;
	}

}

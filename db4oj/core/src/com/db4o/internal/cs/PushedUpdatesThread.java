/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
package com.db4o.internal.cs;

import com.db4o.foundation.*;

public class PushedUpdatesThread extends Thread {
	
//	private final BlockingQueue _committedInfosQueue;

	public PushedUpdatesThread(BlockingQueue committedInfosQueue) {
//		_committedInfosQueue = committedInfosQueue;
	}
	
	public void run () {
		// TODO: send messages to clients.
	}

}

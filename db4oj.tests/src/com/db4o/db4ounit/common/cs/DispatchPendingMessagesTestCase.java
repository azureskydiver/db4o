/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;

import db4ounit.*;


public class DispatchPendingMessagesTestCase extends MessagingTestCaseBase {

	public void testReturnsImmediatelyWithNoMessages() {
		
		final ObjectServer server = openServer(memoryIoConfiguration());
		try {
			final ObjectContainer client = openClient("client", server);
			try {
				
				final AutoStopWatch watch = new AutoStopWatch();
				((ExtClient)client).dispatchPendingMessages(Long.MAX_VALUE);
				Assert.isTrue(watch.peek() < 100);
				
			} finally {
				client.close();
			}
			
		} finally {
			server.close();
		}
	}
}

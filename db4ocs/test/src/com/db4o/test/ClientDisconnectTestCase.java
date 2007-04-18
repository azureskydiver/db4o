/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.internal.cs.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ClientDisconnectTestCase extends Db4oClientServerTestCase {
	
	public static void main(String[] arguments) {
		new ClientDisconnectTestCase().runConcurrency();
	}
	
	public void testDisconnect() throws IOException {
		ExtObjectContainer oc1 = openNewClient();
		ExtObjectContainer oc2 = openNewClient();
		try {
			final ClientObjectContainer client1 = (ClientObjectContainer) oc1;
			final ClientObjectContainer client2 = (ClientObjectContainer) oc2;
			client1.socket().close();
			Assert.isFalse(oc1.isClosed());
			Assert.expect(Db4oException.class, new CodeBlock() {
				public void run() throws Throwable {
					client1.get(null);	
				}
			});
			// It's ok for client2 to get something.
			client2.get(null);
		} finally {
			oc1.close();
			oc2.close();
			Assert.isTrue(oc1.isClosed());
			Assert.isTrue(oc2.isClosed());
		}
	}

	public void concDelete(ExtObjectContainer oc, int seq) throws Exception {
		final ClientObjectContainer client = (ClientObjectContainer) oc;
		try {
			if (seq % 2 == 0) {
				// ok to get something
				client.get(null);
			} else {
				client.socket().close();
				Assert.isFalse(oc.isClosed());
				Assert.expect(Db4oException.class, new CodeBlock() {
					public void run() throws Throwable {
						client.get(null);	
					}
				});
			}
		} finally {
			oc.close();
			Assert.isTrue(oc.isClosed());
		}
	}
}

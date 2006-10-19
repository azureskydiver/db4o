/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import java.io.IOException;

import com.db4o.cs.YapClient;
import com.db4o.ext.Db4oException;
import com.db4o.ext.ExtObjectContainer;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class ClientDisconnect extends ClientServerTestCase {
	
	public static void main(String[] arguments) {
		new ClientDisconnect().runClientServer();
	}
	
	public void testDisconnect() throws IOException {
		ExtObjectContainer oc1 = db();
		ExtObjectContainer oc2 = db();
		try {
			YapClient client1 = (YapClient) oc1;
			YapClient client2 = (YapClient) oc2;
			client1.socket().close();
			Assert.isFalse(oc1.isClosed());
			try {
				client1.get(null);
				Assert.fail("expected exception on get after close");
			} catch (Db4oException exc) {
				// OK, expected
			}
			// It's ok for client2 to get something.
			client2.get(null);
		} finally {
			oc1.close();
			oc2.close();
			// FIXME: The following assertion fails, YapClient#close2 should
			// invoke super.close2() even if the socket is closed.
			Assert.isTrue(oc1.isClosed());
			Assert.isTrue(oc2.isClosed());
		}
	}

	public void concDelete(ExtObjectContainer oc, int seq) throws Exception {
		YapClient client = (YapClient) oc;
		try {
			if (seq % 2 == 0) {
				// ok to get something
				client.get(null);
			} else {
				client.socket().close();
				Assert.isFalse(oc.isClosed());
				try {
					client.get(null);
					Assert.fail("expected exception on get after close");
				} catch (Db4oException exc) {
					// OK, expected
				}
			}
		} finally {
			oc.close();
			Assert.isTrue(oc.isClosed());
		}
	}
}

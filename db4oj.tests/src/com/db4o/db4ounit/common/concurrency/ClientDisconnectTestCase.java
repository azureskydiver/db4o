/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.concurrency;

import com.db4o.cs.events.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.internal.cs.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ClientDisconnectTestCase extends Db4oClientServerTestCase {
	
	public static void main(String[] arguments) {
        new ClientDisconnectTestCase().runConcurrency();
        new ClientDisconnectTestCase().runConcurrency();
	}
	
	public void _concDelete(ExtObjectContainer oc, int seq) throws Exception {
		final ClientObjectContainer client = (ClientObjectContainer) oc;
		ClientEventRegistryFactory.forClient(client).clientSocketReadTimeout().addListener(new EventListener4() {
		    public void onEvent(Event4 e, EventArgs args) {
		        CancellableEventArgs cancellableArgs = (CancellableEventArgs) args;
		        cancellableArgs.cancel();
		    }
		});
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

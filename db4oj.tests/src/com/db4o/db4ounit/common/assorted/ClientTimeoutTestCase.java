/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.cs.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

/**
 * @exclude
 */
public class ClientTimeoutTestCase extends AbstractDb4oTestCase implements OptOutSolo {

	public static void main(String[] arguments) {
		new ClientTimeoutTestCase().runClientServer();
	}

	protected void configure(Configuration config) {
		config.clientServer().timeoutPingClients(10);
		config.clientServer().timeoutClientSocket(10);
	}

	public void _test() throws Exception {
		AbstractClientServerDb4oFixture fixture = (AbstractClientServerDb4oFixture) fixture();
		ObjectServerImpl serverImpl = (ObjectServerImpl) fixture.server();
		Iterator4 iter = serverImpl.iterateDispatchers();
		iter.moveNext();
		ServerMessageDispatcher serverDispatcher = (ServerMessageDispatcher) iter.current();
		ClientMessageDispatcher clientDispatcher = ((ClientObjectContainer) db())
			.messageDispatcher();
		clientDispatcher.close();
		Cool.sleepIgnoringInterruption(1000);
		Assert.isFalse(serverDispatcher.isMessageDispatcherAlive());
	}

}

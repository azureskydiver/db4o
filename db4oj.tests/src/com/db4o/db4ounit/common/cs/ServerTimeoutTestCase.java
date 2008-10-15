/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.cs.*;

import db4ounit.*;

/**
 * @exclude
 */
public class ServerTimeoutTestCase extends ClientServerTestCaseBase {

	public static void main(String[] arguments) {
		new ServerTimeoutTestCase().runClientServer();
	}

	protected void configure(Configuration config) {
		config.clientServer().timeoutClientSocket(1);
		config.clientServer().timeoutServerSocket(1);
	}

	public void _test() throws Exception {
		ObjectServerImpl serverImpl = (ObjectServerImpl) clientServerFixture().server();
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

/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.db4ounit.common.cs;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.cs.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ServerClosedTestCase extends Db4oClientServerTestCase {
	public static void main(String[] args) {
		new ServerClosedTestCase().runAll();
	}

	public void test() throws Exception {
		ExtObjectContainer db = fixture().db();
		ObjectServerImpl serverImpl = (ObjectServerImpl) clientServerFixture()
				.server();
		try {
			Iterator4 iter = serverImpl.iterateDispatchers();
			iter.moveNext();
			ServerMessageDispatcherImpl serverDispatcher = (ServerMessageDispatcherImpl) iter
					.current();
			serverDispatcher.socket().close();
			Cool.sleepIgnoringInterruption(1000);
			Assert.isTrue(db.isClosed());
		} finally {
			serverImpl.close();
		}
	}
}

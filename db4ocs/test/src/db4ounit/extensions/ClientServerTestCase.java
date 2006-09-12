/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package db4ounit.extensions;

import db4ounit.TestCase;
import db4ounit.TestLifeCycle;
import db4ounit.extensions.fixtures.Db4oClientServer;

public class ClientServerTestCase extends Db4oTestCase implements TestCase, TestLifeCycle {

	@Override
	public Db4oClientServer fixture() {
		return (Db4oClientServer) super.fixture();
	}

	
}

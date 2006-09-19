/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package db4ounit.extensions;

import com.db4o.ext.ExtObjectContainer;

import db4ounit.TestCase;
import db4ounit.TestLifeCycle;
import db4ounit.extensions.fixtures.Db4oClientServer;

public class ClientServerTestCase extends AbstractDb4oTestCase implements
		TestCase, TestLifeCycle {

	@Override
	public void setUp() throws Exception {
		Db4oFixture fixture = fixture();
		fixture.clean();
		configure();
		fixture.open();
		ExtObjectContainer oc = fixture.db();
		try {
			store(oc);
		} finally {
			oc.close();
		}
		fixture.close();
		fixture.open();
	}

	protected void store(ExtObjectContainer oc) throws Exception {

	}

	@Override
	public Db4oClientServer fixture() {
		return (Db4oClientServer) super.fixture();
	}

}

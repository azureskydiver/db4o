/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package db4ounit.extensions;

import com.db4o.config.Configuration;
import com.db4o.ext.ExtObjectContainer;

import db4ounit.TestCase;
import db4ounit.TestLifeCycle;

public class ClientServerTestCase extends AbstractDb4oTestCase implements
		TestCase, TestLifeCycle {

	
	protected void configure(Configuration config) {
		super.configure(config);
	}

	public void setUp() throws Exception {
		Db4oFixture fixture = fixture();
		fixture.clean();
		configure(fixture().config());
		fixture.open();
		ExtObjectContainer oc = fixture.db();
		try {
			store(oc);
		} finally {
			oc.close();
		}
		fixture().close();
		fixture().open();
	}

	public ExtObjectContainer db() {
		ExtObjectContainer oc = fixture().db();
		return oc;
	}
	
	protected void store(ExtObjectContainer oc) throws Exception {

	}
}

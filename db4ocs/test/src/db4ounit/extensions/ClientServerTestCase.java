/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package db4ounit.extensions;

import com.db4o.Db4o;
import com.db4o.ext.ExtObjectContainer;

import db4ounit.TestCase;
import db4ounit.TestLifeCycle;

public class ClientServerTestCase extends AbstractDb4oTestCase implements
		TestCase, TestLifeCycle {

	@Override
	public void setUp() throws Exception {
		Db4oFixture fixture = fixture();
		fixture.clean();
		configure(Db4o.cloneConfiguration());
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
		configure(oc);
		return oc;
	}
	
	protected void store(ExtObjectContainer oc) throws Exception {

	}

	protected void configure(ExtObjectContainer oc) {
		
	}
}

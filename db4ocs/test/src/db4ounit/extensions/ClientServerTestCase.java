/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package db4ounit.extensions;

import com.db4o.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

public class ClientServerTestCase extends AbstractDb4oTestCase implements
		TestCase, TestLifeCycle {

	public ObjectServer server() {
		return ((AbstractClientServerDb4oFixture)fixture()).server();
	}

	protected final void store() throws Exception {
		ExtObjectContainer oc = fixture().db();
		try {
			store(oc);
		} finally {
			oc.close();
		}
	}
	
	protected void store(ExtObjectContainer oc) throws Exception {
	}
}

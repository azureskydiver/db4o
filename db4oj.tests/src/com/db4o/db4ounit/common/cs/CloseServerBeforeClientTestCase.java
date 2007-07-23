/* Copyright (C) 2004 - 2007 db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.ext.*;

import db4ounit.extensions.*;

public class CloseServerBeforeClientTestCase extends Db4oClientServerTestCase {

	public static void main(String[] arguments) {
		new CloseServerBeforeClientTestCase().runClientServer();
	}

	public void test() throws Exception {
		ExtObjectContainer client = openNewClient();
		try {
			clientServerFixture().server().close();
		} finally {
			client.close();
		}

	}
}

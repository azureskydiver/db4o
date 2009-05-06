/* Copyright (C) 2004 - 2007 Versant Inc.   http://www.db4o.com */

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
			try{
				client.close();
			} catch(Db4oException e) {
				// database may have been closed
			}
			
			try{
				fixture().close();
			} catch(Db4oException e) {
				// database may have been closed
			}
		}

	}

}

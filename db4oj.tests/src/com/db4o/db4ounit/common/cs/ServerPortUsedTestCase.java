/* Copyright (C) 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.io.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ServerPortUsedTestCase extends Db4oClientServerTestCase {

	private static final String DATABASE_FILE = "PortUsed.db";

	public static void main(String[] args) {
		new ServerPortUsedTestCase().runAll();
	}

	protected void db4oTearDownBeforeClean() throws Exception {
		File4.delete(DATABASE_FILE);

	}

	public void test() {
		final int port = clientServerFixture().serverPort();
		Assert.expect(Db4oIOException.class, new CodeBlock() {
			public void run() throws Throwable {
				Db4o.openServer(DATABASE_FILE, port);
			}
		});

	}
}
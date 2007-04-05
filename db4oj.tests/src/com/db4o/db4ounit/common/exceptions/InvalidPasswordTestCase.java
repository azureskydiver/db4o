/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.db4ounit.common.exceptions;

import java.io.*;

import com.db4o.*;
import com.db4o.internal.cs.*;

import db4ounit.*;

public class InvalidPasswordTestCase implements TestCase, TestLifeCycle {

	private static final String DB_FILE = "server.db4o";

	private static final int PORT = 0xdb40;

	private static final String USER = "db4o";

	private static final String PASSWORD = "db4o";

	private ObjectServer _server;

	public void setUp() throws Exception {
		new File(DB_FILE).delete();
		_server = Db4o.openServer(DB_FILE, PORT);
		_server.grantAccess(USER, PASSWORD);
	}

	public void tearDown() throws Exception {
		_server.close();
	}

	public void testInvalidPassword() {
		Assert.expect(InvalidPasswordException.class, new CodeBlock() {
			public void run() throws Throwable {
				Db4o.openClient("127.0.0.1", PORT, "hello", "invalid");
			}

		});
	}
	
	public void testEmptyPassword() {
		Assert.expect(InvalidPasswordException.class, new CodeBlock() {
			public void run() throws Throwable {
				Db4o.openClient("127.0.0.1", PORT, "", "");
			}
		});
		
		Assert.expect(InvalidPasswordException.class, new CodeBlock() {
			public void run() throws Throwable {
				Db4o.openClient("127.0.0.1", PORT, "", null);
			}
		});
		
		Assert.expect(InvalidPasswordException.class, new CodeBlock() {
			public void run() throws Throwable {
				Db4o.openClient("127.0.0.1", PORT, null, null);
			}
		});
	}
}

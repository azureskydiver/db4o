/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.cs.*;

import db4ounit.*;
import db4ounit.extensions.*;

// TODO fix db4ounit call logic - this should actually be run in C/S mode
public abstract class StandaloneCSTestCaseBase implements TestCase {

	private int _port;

	public static final class Item {
	}
	
	public void test() throws Throwable {
		final Configuration config = Db4o.configure();
		configure(config);
		
		String fileName = databaseFile();
		File4.delete(fileName);
		final ObjectServer server = Db4o.openServer(fileName, -1);
		_port = server.ext().port();
		try {
			server.grantAccess("db4o", "db4o");
			
			runTest();
			
		} finally {
			server.close();
			File4.delete(fileName);
		}
	}

	protected void withClient(ContainerBlock block) throws Throwable {
		ContainerServices.withContainer(openClient(), block);
	}

	protected ClientObjectContainer openClient() {
		return (ClientObjectContainer)Db4o.openClient("localhost", _port, "db4o", "db4o");
	}

	protected int port() {
		return _port;
	}
	
	protected abstract void runTest() throws Throwable;

	protected abstract void configure(Configuration config);

	private String databaseFile() {
		return Path4.combine(Path4.getTempPath(), "cc.db4o");
	}

}
/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package db4ounit.extensions;

import java.io.File;
import java.io.IOException;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.test.config.Configure;

import db4ounit.TestCase;
import db4ounit.TestLifeCycle;

public class ClientServerTestCase implements TestCase, TestLifeCycle {

	public static String TEST_SERVER_FILENAME = "server.yap";

	transient protected ObjectServer server;

	protected ObjectContainer oc;

	public void setUp() throws Exception {
		// start test server
		// TODO: start server in seperate vm
		configure();
		server = Db4o.openServer(TEST_SERVER_FILENAME, Configure.PORT);
		server.grantAccess(Configure.USER, Configure.PASSWORD);
		store();
	}

	protected ObjectContainer openClient() throws IOException {
		return Db4o.openClient("localhost", Configure.PORT, Configure.USER,
				Configure.PASSWORD);
	}

	protected void configure() {
		// default: do nothing
	}

	protected void store() throws Exception {
		// default: do nothing
	}

	public void tearDown() throws Exception {
		// tear down test server
		server.close();
		File file = new File(TEST_SERVER_FILENAME);
		file.delete();
	}

}

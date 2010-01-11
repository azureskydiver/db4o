/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.omplus.connection.test;

import static com.db4o.omplus.test.util.Db4oTestUtil.*;
import static org.junit.Assert.*;

import java.io.*;

import org.junit.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.*;
import com.db4o.cs.config.*;
import com.db4o.omplus.connection.*;

public class RemoteConnectionParamsTestCase {

	private static final String HOST = "localhost";
	private static final String USER = "db4o_user";
	private static final String PASSWORD = "db4o_pass";

	@Test
	public void testNoServer() {
		RemoteConnectionParams params = new RemoteConnectionParams(HOST, 0xdb40, USER, PASSWORD);
		try {
			params.connect();
			fail();
		} 
		catch (DBConnectException exc) {
		}
	}

	@Test
	public void testWrongCredentials() throws Exception {
		File dbFile = createEmptyDatabase();
		ObjectServer server = openServer(dbFile);
		RemoteConnectionParams params = new RemoteConnectionParams(HOST, server.ext().port(), USER, PASSWORD + "X");
		try {
			params.connect();
			fail();
		} 
		catch (DBConnectException exc) {
		}
		finally {
			server.close();
			dbFile.delete();
		}
	}

	@Test
	public void testOpen() throws Exception {
		File dbFile = createEmptyDatabase();
		ObjectServer server = openServer(dbFile);
		RemoteConnectionParams params = new RemoteConnectionParams(HOST, server.ext().port(), USER, PASSWORD);
		try {
			ObjectContainer client = params.connect();
			assertNotNull(client);
			client.close();
		}
		finally {
			server.close();
			dbFile.delete();
		}
	}

	private ObjectServer openServer(File dbFile) {
		ServerConfiguration serverConfig = Db4oClientServer.newServerConfiguration();
		// FIXME: getting ObjectNotStorableException for ClassInfo if DotNetSupport is not added on server, too?!?
		serverConfig.common().add(new DotnetSupport(true));
		ObjectServer server = Db4oClientServer.openServer(serverConfig, dbFile.getAbsolutePath(), -1);
		server.grantAccess(USER, PASSWORD);
		return server;
	}

}

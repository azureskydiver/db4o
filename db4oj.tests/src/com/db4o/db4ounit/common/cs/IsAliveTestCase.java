package com.db4o.db4ounit.common.cs;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.cs.*;

import db4ounit.*;

public class IsAliveTestCase  implements TestLifeCycle {
	
	private final static String USERNAME = "db4o";
	private final static String PASSWORD = "db4o";
	
	private String filePath;

	public void testIsAlive() {
		ObjectServer server = openServer();
		int port = server.ext().port();
		ClientObjectContainer client = openClient(port);
		Assert.isTrue(client.isAlive());
		client.close();
		server.close();
	}

	public void testIsNotAlive() {
		ObjectServer server = openServer();
		int port = server.ext().port();
		ClientObjectContainer client = openClient(port);
		server.close();
		Assert.isFalse(client.isAlive());
		client.close();
	}

	public void setUp() throws Exception {
		filePath = Path4.getTempFileName();
		File4.delete(filePath);
	}

	public void tearDown() throws Exception {
		File4.delete(filePath);
	}
	
	private Configuration config() {
		return Db4o.newConfiguration();
	}

	private ObjectServer openServer() {
		ObjectServer server = Db4o.openServer(config(), filePath, -1);
		server.grantAccess(USERNAME, PASSWORD);
		return server;
	}

	private ClientObjectContainer openClient(int port) {
		ClientObjectContainer client = (ClientObjectContainer) Db4o.openClient(config(), "localhost", port, USERNAME, PASSWORD);
		return client;
	}

}

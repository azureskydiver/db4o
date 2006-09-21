/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import java.io.*;

import com.db4o.*;
import com.db4o.ext.ExtObjectContainer;

import db4ounit.TestException;
import db4ounit.extensions.Db4oFixture;

public class Db4oClientServer implements Db4oFixture {
	private static final String HOST = "localhost";

	private static final String USERNAME = "db4o";

	private static final String PASSWORD = USERNAME;

	private ObjectServer _server;

	private final int _port;

	private final File _yap;

	public Db4oClientServer(String fileName, int port) {
		_yap = new File(fileName);
		_port = port;
	}

	public void close() throws Exception {
		_server.close();
	}

	public void open() throws Exception {
		_server = Db4o.openServer(_yap.getAbsolutePath(), _port);
		_server.grantAccess(USERNAME, PASSWORD);
	}

	public ExtObjectContainer db() {
		try {
			return Db4o.openClient(HOST, _port, USERNAME, PASSWORD).ext();
		} catch (IOException e) {
			e.printStackTrace();
			throw new TestException(e);
		}
	}

	public void clean() {
		_yap.delete();
	}
}

/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectServer;
import com.db4o.ext.ExtObjectContainer;

import db4ounit.extensions.Db4oFixture;

public class Db4oClientServer implements Db4oFixture {
	private static final String HOST = "localhost";
	private static final String USERNAME = "db4o";
	private static final String PASSWORD = USERNAME;
	
	File _yap;
	private ObjectServer _server;
	private ExtObjectContainer _db;
	private int _port;
	
	public Db4oClientServer(String fileName, int port) {
		_yap = new File(fileName);
		_port = port;
	}

	public void clean() {
		_yap.delete();
	}

	public void close() throws Exception {
		_db.close();
		_server.close();
	}

	public ExtObjectContainer db() {
		return _db;
	}

	public void open() throws Exception {
		_server = Db4o.openServer(_yap.getCanonicalPath(), _port);
		_server.grantAccess(USERNAME, PASSWORD);
		_db = (ExtObjectContainer) Db4o.openClient(HOST, _port, USERNAME, PASSWORD);
	}
}

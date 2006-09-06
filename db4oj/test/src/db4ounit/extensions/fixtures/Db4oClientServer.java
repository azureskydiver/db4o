/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import com.db4o.*;
import com.db4o.ext.ExtObjectContainer;

public class Db4oClientServer extends AbstractFileBasedDb4oFixture {
	private static final String HOST = "localhost";
	private static final String USERNAME = "db4o";
	private static final String PASSWORD = USERNAME;
	
	private ObjectServer _server;
	private final int _port;
	
	public Db4oClientServer(String fileName, int port) {
		super(fileName);		
		_port = port;
	}

	public void close() throws Exception {
		super.close();
		_server.close();
	}

	public void open() throws Exception {
		_server = Db4o.openServer(getAbsolutePath(), _port);
		_server.grantAccess(USERNAME, PASSWORD);
		db((ExtObjectContainer) Db4o.openClient(HOST, _port, USERNAME, PASSWORD));
	}	
}

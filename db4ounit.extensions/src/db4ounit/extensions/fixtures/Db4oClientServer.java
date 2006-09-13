/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import java.io.IOException;

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
		_server.close();
	}

	public void open() throws Exception {
		_server = Db4o.openServer(getAbsolutePath(), _port);
	}

	public ExtObjectContainer openClient() throws IOException {
		_server.grantAccess(USERNAME, PASSWORD);
		return Db4o.openClient(HOST, _port, USERNAME, PASSWORD).ext();
	}

	public ExtObjectContainer db() {
		throw new UnsupportedOperationException();
	}

	protected void db(ExtObjectContainer container) {
		throw new UnsupportedOperationException();
	}	
	
	
}

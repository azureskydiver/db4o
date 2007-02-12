/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import java.io.IOException;

import com.db4o.Db4o;
import com.db4o.ObjectServer;
import com.db4o.drs.db4o.Db4oProviderFactory;
import com.db4o.ext.ExtObjectContainer;

public class Db4oClientServerDrsFixture extends Db4oDrsFixture {
	private static final String HOST = "localhost";
	private static final String USERNAME = "db4o";
	private static final String PASSWORD = USERNAME;
	
	private ObjectServer _server;
	private int _port;
	
	public Db4oClientServerDrsFixture(String name, int port) {
		super(name);
		_port = port;
	}

	public void close(){
		super.close();
		_server.close();
	}

	public void open(){
		Db4o.configure().messageLevel(-1);
		
		_server = Db4o.openServer(testFile.getPath(), _port);
		_server.grantAccess(USERNAME, PASSWORD);
		try {
			_db = (ExtObjectContainer) Db4o.openClient(HOST, _port, USERNAME, PASSWORD);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		_provider = Db4oProviderFactory.newInstance(_db, _name);
	}
}
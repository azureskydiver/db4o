/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.test.replication.db4ounit.fixtures;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectServer;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.test.replication.db4ounit.DrsFixture;


public class Db4oClientServerDrsFixture implements DrsFixture {

	private static final String HOST = "localhost";
	private static final String USERNAME = "db4o";
	private static final String PASSWORD = USERNAME;
	
	private String _name;
	private ObjectServer _server;
	private ExtObjectContainer _db;
	private TestableReplicationProviderInside _provider;
	private int _port;
	
	public Db4oClientServerDrsFixture(String name, int port) {
		_name = name;
		_port = port;
	}

	public TestableReplicationProviderInside provider() {
		return _provider;
	}
	
	// FIXME: escape _name
	private String yapFileName() {
		return "drs_cs_" + _name + ".yap";
	}

	public void clean() {
		new File(yapFileName()).delete();
	}

	public void close() throws Exception {
		_db.close();
		_provider.destroy();
		_server.close();
	}

	public ExtObjectContainer db() {
		return _db;
	}

	public void open() throws Exception {
		_server = Db4o.openServer(yapFileName(), _port);
		_server.grantAccess(USERNAME, PASSWORD);
		_db = (ExtObjectContainer) Db4o.openClient(HOST, _port, USERNAME, PASSWORD);
		_provider = new Db4oReplicationProvider(_db, _name);
	}

}

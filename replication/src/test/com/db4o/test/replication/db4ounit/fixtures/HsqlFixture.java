/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.test.replication.db4ounit.fixtures;

import java.io.File;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import com.db4o.Db4o;
import com.db4o.ObjectServer;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.io.MemoryIoAdapter;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.test.replication.db4ounit.DrsFixture;

public class HsqlFixture implements DrsFixture {

	private static final String HOST = "localhost";
	private static final String USERNAME = "db4o";
	private static final String PASSWORD = USERNAME;
	
	private String _name;
	private ObjectServer _server;
	private ExtObjectContainer _db;
	private TestableReplicationProviderInside _provider;
	private int _port;
	
	public HsqlFixture(String name, int port) {
		_name = name;
		_port = port;
	}

	public TestableReplicationProviderInside provider() {
		return _provider;
	}

	public void clean() {
		//do nothing
	}

	public void close() {
		_db.close();
		_provider.destroy();
		_server.close();
	}

	public ExtObjectContainer db() {
		return _db;
	}

	public void open()  {
//		Configuration configuration = new Configuration().configure(HSQL_CFG_XML);
//		String url = JDBC_URL_HEAD + jdbcUrlCounter++;
//		return configuration.setProperty(Environment.URL, url);
	}

}

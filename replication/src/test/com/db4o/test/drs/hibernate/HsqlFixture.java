/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.test.drs.hibernate;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.impl.HibernateReplicationProviderImpl;

public class HsqlFixture extends RdbmsFixture {
	private static final String HSQL_CFG_XML = "com/db4o/test/drs/hibernate/hibernate-HSQL.cfg.xml";
	private static final String JDBC_URL_HEAD = "jdbc:hsqldb:mem:unique_";
	private static int jdbcUrlCounter = 0;
	
	private String _name;
	
	private TestableReplicationProviderInside _provider;
	
	public HsqlFixture(String name) {
		_name = name;
	}

	public TestableReplicationProviderInside provider() {
		return _provider;
	}

	public void clean() {
		//do nothing
	}

	public void close() {
		_provider.destroy();
	}

	public void open()  {
		Configuration configuration = createConfig().configure(HSQL_CFG_XML);
		String url = JDBC_URL_HEAD + jdbcUrlCounter++;
		_provider = new HibernateReplicationProviderImpl(configuration.setProperty(Environment.URL, url), _name);
	}
}

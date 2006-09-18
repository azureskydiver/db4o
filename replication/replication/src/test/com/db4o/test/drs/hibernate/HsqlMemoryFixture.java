/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.test.drs.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Environment;

import com.db4o.replication.hibernate.impl.HibernateReplicationProviderImpl;

public class HsqlMemoryFixture extends RdbmsFixture {
	private static final String HSQL_CFG_XML = "com/db4o/test/drs/hibernate/Hsql.cfg.xml";
	private static final String JDBC_URL_HEAD = "jdbc:hsqldb:mem:unique_";
	private static int jdbcUrlCounter = 0;
		
	public HsqlMemoryFixture(String name) {
		super(name);
	}

	public void clean() {
		if (config==null)
			return;
		
		SessionFactory sf = createConfig().configure(HSQL_CFG_XML)
			.setProperty(Environment.URL, dbUrl).buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();

		try {
			session.connection().createStatement().execute("SHUTDOWN IMMEDIATELY");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	
		tx.commit();
		session.close();
		sf.close();
	}

	public void open()  {
		config = createConfig().configure(HSQL_CFG_XML);
		dbUrl = JDBC_URL_HEAD + jdbcUrlCounter++;
		_provider = new HibernateReplicationProviderImpl(config.setProperty(Environment.URL, dbUrl), _name);
	}
}

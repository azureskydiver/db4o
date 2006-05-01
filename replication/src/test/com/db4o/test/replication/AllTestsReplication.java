/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.replication.hibernate.cfg.ReplicationConfiguration;
import com.db4o.test.AllTests;
import com.db4o.test.Test;
import com.db4o.test.TestSuite;
import com.db4o.test.replication.db4o.Db4oReplicationTestUtil;
import com.db4o.test.replication.hibernate.HibernateUtil;
import com.db4o.test.replication.transients.TransientReplicationProvider;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class AllTestsReplication extends AllTests {

	public static void main(String[] args) {
		new AllTestsReplication().run();
	}

	public void run() {
		Test.clientServer = false;

		Db4oReplicationTestUtil.configure();
		registerProviderPairs();
		super.run();
		Db4oReplicationTestUtil.close();
	}

	private void registerProviderPairs() {
		transients();
		hsql();
		db4o();

//		transientdb4o();
//		hsqlDb4o();
//		db4oHsql();

		//oracle();
	}

	private void db4oHsql() {
		ReplicationTestCase.registerProviderPair(Db4oReplicationTestUtil.newProviderA(), HibernateUtil.newProviderB());
	}

	private void hsqlDb4o() {
		ReplicationTestCase.registerProviderPair(HibernateUtil.newProviderA(), Db4oReplicationTestUtil.newProviderB());
	}

	private void transients() {
		ReplicationTestCase.registerProviderPair(new TransientReplicationProvider(new byte[]{65}, "Transient A"), new TransientReplicationProvider(new byte[]{66}, "Transient B"));
	}

	private void transientdb4o() {
		ReplicationTestCase.registerProviderPair(new TransientReplicationProvider(new byte[]{65,58,89}, "Transient A"), Db4oReplicationTestUtil.newProviderB());
	}

	private void db4o() {
		ReplicationTestCase.registerProviderPair(Db4oReplicationTestUtil.newProviderA(), Db4oReplicationTestUtil.newProviderB());
	}

	private void hsql() {ReplicationTestCase.registerProviderPair(HibernateUtil.newProviderA(), HibernateUtil.newProviderB());}

	private void oracle() {
		Configuration tmp = HibernateUtil.oracleConfigA();
		ReplicationConfiguration.decorate(tmp);
		HibernateUtil.addAllMappings(tmp);
		new SchemaExport(tmp).drop(false, true);

		ReplicationTestCase.registerProviderPair(HibernateUtil.newOracleProviderA(), new TransientReplicationProvider(new byte[]{66}, "B"));
	}

	protected void addTestSuites(TestSuite suites) {
		CLIENT_SERVER = false;
		suites.add(new ReplicationTestSuite());
	}
}

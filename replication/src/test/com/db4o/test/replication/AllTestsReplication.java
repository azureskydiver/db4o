/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.Debug;
import com.db4o.foundation.Debug4;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.replication.hibernate.impl.HibernateReplicationProviderImpl;
import com.db4o.replication.hibernate.impl.ReplicationConfiguration;
import com.db4o.test.AllTests;
import com.db4o.test.AllTestsConfAll;
import com.db4o.test.Test;
import com.db4o.test.TestSuite;
import com.db4o.test.replication.db4o.Db4oReplicationTestUtil;
import com.db4o.test.replication.hibernate.HibernateUtil;
import com.db4o.test.replication.transients.TransientReplicationProvider;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import java.io.File;

public class AllTestsReplication extends AllTests {

	public static void main(String[] args) {
		new AllTestsReplication().run();
	}

	public void run() {
		new File(AllTestsConfAll.FILE_SERVER).delete();

		Test.clientServer = false;
		//Test.clientServer = true;
		//Debug.longTimeOuts = true; //ReplicationFeaturesMain fails if set to false in C/S

		Db4oReplicationTestUtil.configure();
		registerProviderPairs();
		super.run();
		Db4oReplicationTestUtil.close();
	}

	private void registerProviderPairs() {
		db4o();

		transients();
		hsql();
		db4otransient();
		hsqlDb4o();
		db4oHsql();

		//oracle();
		//mysql();
		//postgresql();
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

	private void db4otransient() {
		ReplicationTestCase.registerProviderPair(Db4oReplicationTestUtil.newProviderA(), new TransientReplicationProvider(new byte[]{65, 58, 89}, "Transient B"));
	}

	private void db4o() {
		if (Test.clientServer) {
			//Db4oClientServerReplicationProvider providerA = new Db4oClientServerReplicationProvider(Test.objectContainer(), "db4o C/S", Test.SERVER_HOSTNAME, Test.SERVER_PORT, Test.DB4O_USER, Test.DB4O_PASSWORD);
			ReplicationTestCase.registerProviderPair(new Db4oReplicationProvider(Test.objectContainer(), "db4o C/S a"), Db4oReplicationTestUtil.newProviderB());
		} else
			ReplicationTestCase.registerProviderPair(Db4oReplicationTestUtil.newProviderA(), Db4oReplicationTestUtil.newProviderB());
	}

	private void hsql() {ReplicationTestCase.registerProviderPair(HibernateUtil.newProviderA(), HibernateUtil.newProviderB());}

	private void oracle() {
		Configuration tmp = HibernateUtil.oracleConfigA();
		cleanDb(tmp);
		ReplicationTestCase.registerProviderPair(HibernateUtil.newOracleProviderA(), new TransientReplicationProvider(new byte[]{66}, "B"));
	}

	private void mysql() {
		Configuration tmp = HibernateUtil.produceMySQLConfigA();
		cleanDb(tmp);
		ReplicationTestCase.registerProviderPair(new HibernateReplicationProviderImpl(tmp, "mysql"), new TransientReplicationProvider(new byte[]{69}, "B"));
	}

	private void postgresql() {
		Configuration a = HibernateUtil.producePostgreSQLConfigA();
		cleanDb(a);

		Configuration b = HibernateUtil.producePostgreSQLConfigB();
		cleanDb(b);
		ReplicationTestCase.registerProviderPair(new HibernateReplicationProviderImpl(a, "postgresql"), new HibernateReplicationProviderImpl(b, "postgresql B"));
	}

	private void cleanDb(Configuration tmp) {
		ReplicationConfiguration.decorate(tmp);
		HibernateUtil.addAllMappings(tmp);
		new SchemaExport(tmp).drop(false, true);
	}

	protected void addTestSuites(TestSuite suites) {
		CLIENT_SERVER = false;
		suites.add(new ReplicationTestSuite());
	}
}

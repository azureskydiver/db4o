/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication;

import java.io.File;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import com.db4o.drs.db4o.Db4oReplicationProvider;
import com.db4o.drs.hibernate.impl.HibernateReplicationProviderImpl;
import com.db4o.drs.hibernate.impl.ReplicationConfiguration;
import com.db4o.drs.inside.TestableReplicationProviderInside;
import com.db4o.test.AllTests;
import com.db4o.test.AllTestsConfAll;
import com.db4o.test.Test;
import com.db4o.test.TestSuite;
import com.db4o.test.replication.db4o.Db4oReplicationTestUtil;
import com.db4o.test.replication.hibernate.HibernateUtil;
import com.db4o.test.replication.transients.TransientReplicationProvider;

public class AllTestsReplication extends AllTests {

	public static void main(String[] args) {
		new AllTestsReplication().run();
	}

	public void run() {
		new File(AllTestsConfAll.FILE_SERVER).delete();

		Test.clientServer = true;
		//Test.clientServer = false;
		//Debug.longTimeOuts = true; //ReplicationFeaturesMain fails if set to false in C/S

		Db4oReplicationTestUtil.configure();
		registerProviderPairs();
		super.run();
		Db4oReplicationTestUtil.close();
	}

	private void registerProviderPairs() {
		// In SOLO, you can run all combinations together
		// In C/S, you can't run all combinations together, it causes db4o connection to timeout.

		//db4o();
		//transients();
		//hsql();
		//transienthsql();
		//hsqltransient();
		//db4otransient();
	//	hsqlDb4o();
		//db4oHsql();

		oracle();
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
		final TransientReplicationProvider transients = new TransientReplicationProvider(new byte[]{65, 58, 89}, "Transient B");
		final TestableReplicationProviderInside db4o;
		
		if (Test.clientServer)
			db4o = new Db4oReplicationProvider(Test.objectContainer(), "db4o C/S a");
		else
			db4o = Db4oReplicationTestUtil.newProviderA();		
		
		ReplicationTestCase.registerProviderPair(db4o, transients);
	}

	private void db4o() {
		if (Test.clientServer) {
			//Db4oClientServerReplicationProvider providerA = new Db4oClientServerReplicationProvider(Test.objectContainer(), "db4o C/S", Test.SERVER_HOSTNAME, Test.SERVER_PORT, Test.DB4O_USER, Test.DB4O_PASSWORD);
			ReplicationTestCase.registerProviderPair(new Db4oReplicationProvider(Test.objectContainer(), "db4o C/S a"), Db4oReplicationTestUtil.newProviderB());
		} else
			ReplicationTestCase.registerProviderPair(Db4oReplicationTestUtil.newProviderA(), Db4oReplicationTestUtil.newProviderB());
	}

	private void hsql() {ReplicationTestCase.registerProviderPair(HibernateUtil.newProviderA(), HibernateUtil.newProviderB());}

	
	private void transienthsql() {
		ReplicationTestCase.registerProviderPair(new TransientReplicationProvider(new byte[]{65,36,48}), HibernateUtil.newProviderB());
	}
	
	private void hsqltransient() {
		ReplicationTestCase.registerProviderPair(HibernateUtil.newProviderA(), new TransientReplicationProvider(new byte[]{65,36,49}));
	}
	
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
		//suites.add(new com.db4o.test.other.AllTests() );
	}
}

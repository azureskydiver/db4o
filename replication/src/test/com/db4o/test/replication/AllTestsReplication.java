/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.*;
import com.db4o.test.replication.db4o.*;
import com.db4o.test.replication.hibernate.*;
import com.db4o.test.replication.transients.*;

public class AllTestsReplication extends AllTests {

	public static void main(String[] args) {
		new AllTestsReplication().run();
//        System.exit(0);
	}

	public void run() {
		Db4oReplicationTestUtil.configure();
		registerProviderPairs();
		super.run();
		Db4oReplicationTestUtil.close();
	}

	private void registerProviderPairs() {
//		String[] ignoredMethods = new String[]{"toString", "getMonitor", "getSignature"};
//		TestableReplicationProviderInside comparator = (TestableReplicationProviderInside)ImplementationComparator.createGiven(new TransientReplicationProvider(new byte[]{65}, "refAsTableA"), new TransientReplicationProvider(new byte[]{65}, "refAsTableA"), ignoredMethods);
//		ReplicationTestCase.registerProviderPair(comparator, new TransientReplicationProvider(new byte[]{66}, "B"));

		ReplicationTestCase.registerProviderPair(new TransientReplicationProvider(new byte[]{65}, "A"), new TransientReplicationProvider(new byte[]{66}, "B"));

//		ReplicationTestCase.registerProviderPair(HibernateUtil.refAsTableProviderA(), HibernateUtil.refAsTableProviderB());
//        ReplicationTestcase.registerProviderPair(Db4oReplicationTestUtil.newProviderA(), Db4oReplicationTestUtil.newProviderB());
//        ReplicationTestcase.registerProviderPair(HibernateUtil.produceMySQLConfigA());
	}

	protected void addTestSuites(TestSuite suites) {
		CLIENT_SERVER = false;
		suites.add(new ReplicationTestSuite());
	}
}

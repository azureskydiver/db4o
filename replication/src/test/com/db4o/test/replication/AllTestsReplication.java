/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication;

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
        ReplicationTestcase.registerProviderPair(new TransientReplicationProvider(new byte[] {65}, "A"), new TransientReplicationProvider(new byte[] {66}, "B"));
        ReplicationTestcase.registerProviderPair(HibernateUtil.refAsTableProviderA(), HibernateUtil.refAsTableProviderB());
//      ReplicationTestcase.registerProviderPair(Db4oReplicationTestUtil.newProviderA(), Db4oReplicationTestUtil.newProviderB());
//      ReplicationTestcase.registerProviderPair(HibernateUtil.produceMySQLConfigA());
    }

    protected void addTestSuites(TestSuite suites) {
		CLIENT_SERVER = false;
		suites.add(new ReplicationTestSuite());
	}
}

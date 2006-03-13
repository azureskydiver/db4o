/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.test.AllTests;
import com.db4o.test.TestSuite;
import com.db4o.test.replication.db4o.Db4oReplicationTestUtil;

public class AllTestsReplication extends AllTests {

	public static void main(String[] args) {
		Db4oReplicationTestUtil.configure();
//		runSolo(MapTestHsqlRefAsColumns.class);
//		runSolo(MapTestHsqlRefAsColumns.class);
		new AllTestsReplication().run();
		Db4oReplicationTestUtil.close();
		System.exit(0);
	}

	protected void addTestSuites(TestSuite suites) {
		CLIENT_SERVER = false;
		suites.add(new ReplicationTestSuite());
	}
}

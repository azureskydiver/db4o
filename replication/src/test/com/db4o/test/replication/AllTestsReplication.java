/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.test.AllTests;
import com.db4o.test.TestSuite;
import com.db4o.test.replication.db4o.Db4oReplicationTestUtil;
import com.db4o.test.replication.jdk14.R0to4RunnerCombinations;

public class AllTestsReplication extends AllTests {

	public static void main(String[] args) {
		runSolo(R0to4RunnerCombinations.class);
		//new AllTestsReplication().run();
		Db4oReplicationTestUtil.close();
		System.exit(0);
	}

	protected void addTestSuites(TestSuite suites) {
		CLIENT_SERVER = false;
		suites.add(new ReplicationTestSuite());
	}
}

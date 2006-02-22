package com.db4o.test.replication.hibernate.mysql;

import com.db4o.test.TestSuite;

public class MySQLTestSuite extends TestSuite {
	public Class[] tests() {
		return new Class[]{
				MySQLMapTest.class,
				MySQLListTest.class,
				MySQLR0to4Runner.class,
				MySQLReplicationFeaturesMain.class
		};
	}
}

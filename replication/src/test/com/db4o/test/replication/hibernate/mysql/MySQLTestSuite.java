package com.db4o.test.replication.hibernate.mysql;

import com.db4o.test.TestSuite;

public class MySQLTestSuite extends TestSuite {
	public Class[] tests() {
		return new Class[]{
				MySQLProviderTest.class,
				MySQLReplicationConfiguratorTest.class,
				MySQLMetaDataTablesCreatorTest.class,
		};
	}
}

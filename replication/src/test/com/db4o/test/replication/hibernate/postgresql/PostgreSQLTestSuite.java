package com.db4o.test.replication.hibernate.postgresql;

import com.db4o.test.TestSuite;

public class PostgreSQLTestSuite extends TestSuite {
	public Class[] tests() {
		return new Class[]{
				PostgreSQLProviderTest.class,
				PostgreSQLReplicationConfiguratorTest.class,
				PostgreSQLMetaDataTablesCreatorTest.class,
		};
	}
}

package com.db4o.test.replication.hibernate.ref_as_columns.postgresql;

import com.db4o.test.TestSuite;

public class PostgreSQLTestSuite extends TestSuite {
	public Class[] tests() {
		return new Class[]{
				PostgreSQLMetaDataTablesCreatorTest.class,
				PostgreSQLR0to4Runner.class,
				PostgreSQLReplicationFeaturesMain.class,
				PostgreSQLMapTest.class,
				PostgreSQLListTest.class,
		};
	}
}

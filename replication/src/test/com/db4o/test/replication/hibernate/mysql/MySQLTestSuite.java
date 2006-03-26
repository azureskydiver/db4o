package com.db4o.test.replication.hibernate.mysql;

import com.db4o.test.TestSuite;

public class MySQLTestSuite extends TestSuite {
	public Class[] tests() {
		return new Class[]{
				MySQLListTest.class,

				MySQLFeaturesMain.class,
				MySQLSimpleParentChild.class,
				MySQLAfterDeletion.class,
				MySQLProviderTest.class,
				MySQLReplicationConfiguratorTest.class,
				MySQLMetaDataTablesCreatorTest.class,
				MySQLR0to4Runner.class,
				MySQLMapTest.class,
		};
	}
}

package com.db4o.test.replication.hibernate.oracle;

import com.db4o.test.TestSuite;

public class OracleTestSuite extends TestSuite {
	public Class[] tests() {
		return new Class[]{
				OracleAfterDeletion.class,
				OracleProviderTest.class,
				OracleReplicationConfiguratorTest.class,
				OracleMetaDataTablesCreatorTest.class,
				OracleListTest.class,
				OracleMapTest.class,
				OracleFeaturesMain.class,
				OracleR0to4Runner.class,
		};
	}
}

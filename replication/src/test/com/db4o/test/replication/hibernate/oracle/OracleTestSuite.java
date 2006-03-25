package com.db4o.test.replication.hibernate.oracle;

import com.db4o.test.TestSuite;

public class OracleTestSuite extends TestSuite {
	public Class[] tests() {
		return new Class[]{
				OracleListTest.class,

				OracleFeaturesMain.class,
				OracleSimpleParentChild.class,
				OracleAfterDeletion.class,
				OracleProviderTest.class,
				OracleReplicationConfiguratorTest.class,
				OracleMetaDataTablesCreatorTest.class,
				OracleR0to4Runner.class,
				OracleMapTest.class,
		};
	}
}

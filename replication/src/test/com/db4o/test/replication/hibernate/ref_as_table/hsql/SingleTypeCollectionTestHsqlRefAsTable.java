package com.db4o.test.replication.hibernate.ref_as_table.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.hibernate.ref_as_columns.hsql.HibernateSingleTypeCollectionReplicationTest;
import com.db4o.test.replication.hibernate.ref_as_table.RefAsTableUtil;

public class SingleTypeCollectionTestHsqlRefAsTable extends HibernateSingleTypeCollectionReplicationTest {
	protected TestableReplicationProviderInside prepareProviderA() {
		return RefAsTableUtil.newProvider(newCfg(), "A");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return RefAsTableUtil.newProvider(newCfg(), "B");
	}

	public void testCollectionReplication() {
		super.testCollectionReplication();
	}
}

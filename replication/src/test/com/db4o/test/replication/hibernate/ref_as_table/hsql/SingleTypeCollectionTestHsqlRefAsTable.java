package com.db4o.test.replication.hibernate.ref_as_table.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.SingleTypeCollectionReplicationTest;
import com.db4o.test.replication.hibernate.HibernateUtil;

public class SingleTypeCollectionTestHsqlRefAsTable extends SingleTypeCollectionReplicationTest {
	protected TestableReplicationProviderInside prepareProviderA() {
		return HibernateUtil.refAsTableProviderA();
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return HibernateUtil.refAsTableProviderB();
	}

	public void testCollectionReplication() {
		super.testCollectionReplication();
	}
}

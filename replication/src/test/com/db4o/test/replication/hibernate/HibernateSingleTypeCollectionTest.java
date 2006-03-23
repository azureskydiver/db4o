package com.db4o.test.replication.hibernate;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.SingleTypeCollectionReplicationTest;

public class HibernateSingleTypeCollectionTest extends SingleTypeCollectionReplicationTest {
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

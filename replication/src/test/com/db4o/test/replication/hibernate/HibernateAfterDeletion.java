package com.db4o.test.replication.hibernate;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.ReplicationAfterDeletionTest;

public class HibernateAfterDeletion extends ReplicationAfterDeletionTest {
	protected TestableReplicationProviderInside prepareProviderA() {
		return HibernateUtil.refAsTableProviderA();
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return HibernateUtil.refAsTableProviderB();
	}

	public void test() {
		super.test();
	}
}

package com.db4o.test.replication.hibernate.oracle;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.impl.HibernateReplicationProviderImpl;
import com.db4o.test.replication.ReplicationAfterDeletionTest;
import com.db4o.test.replication.hibernate.HibernateUtil;

public class OracleAfterDeletion extends ReplicationAfterDeletionTest {
	protected TestableReplicationProviderInside prepareProviderA() {
		return new HibernateReplicationProviderImpl(HibernateUtil.produceOracleConfigA());
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return new HibernateReplicationProviderImpl(HibernateUtil.produceOracleConfigB());
	}

	public void test() {
		super.test();
	}
}

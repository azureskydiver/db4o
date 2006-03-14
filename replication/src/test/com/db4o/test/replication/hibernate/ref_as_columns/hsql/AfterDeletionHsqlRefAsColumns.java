package com.db4o.test.replication.hibernate.ref_as_columns.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.hibernate.HibernateAfterDeletion;
import com.db4o.test.replication.hibernate.ref_as_columns.RefAsColumnsUtil;

public class AfterDeletionHsqlRefAsColumns extends HibernateAfterDeletion {
	protected TestableReplicationProviderInside prepareProviderA() {
		return RefAsColumnsUtil.newProviderA();
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return RefAsColumnsUtil.newProviderB();
	}

	public void test() {
		super.test();
	}
}

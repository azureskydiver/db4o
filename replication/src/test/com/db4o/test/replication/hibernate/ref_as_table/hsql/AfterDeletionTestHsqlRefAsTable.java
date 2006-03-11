package com.db4o.test.replication.hibernate.ref_as_table.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.hibernate.HibernateUtil;
import com.db4o.test.replication.hibernate.ref_as_columns.hsql.HibernateReplicationAfterDeletionTest;

public class AfterDeletionTestHsqlRefAsTable extends HibernateReplicationAfterDeletionTest {

	protected TestableReplicationProviderInside prepareProviderA() {
		return HibernateUtil.newRefAsTable(newCfg(), "A");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return HibernateUtil.newRefAsTable(newCfg(), "B");
	}

	public void test() {
		super.test();
	}
}

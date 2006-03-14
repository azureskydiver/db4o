package com.db4o.test.replication.hibernate.ref_as_columns.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.hibernate.HibernateMapTest;
import com.db4o.test.replication.hibernate.HibernateUtil;

public class MapTestHsqlRefAsColumns extends HibernateMapTest {
	protected TestableReplicationProviderInside prepareProviderA() {
		return HibernateUtil.refAsColumnsProviderA();
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return HibernateUtil.refAsColumnsProviderB();
	}

	public void test() {
		super.test();
	}
}

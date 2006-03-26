package com.db4o.test.replication.hibernate.mysql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.impl.HibernateReplicationProviderImpl;
import com.db4o.test.replication.collections.ListTest;
import com.db4o.test.replication.db4o.Db4oReplicationTestUtil;
import com.db4o.test.replication.hibernate.HibernateUtil;

public class MySQLListTest extends ListTest {
	protected TestableReplicationProviderInside prepareProviderA() {
		return new HibernateReplicationProviderImpl(HibernateUtil.produceMySQLConfigA());
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return Db4oReplicationTestUtil.newProviderB();
	}

	public void test() {
		super.test();
	}
}

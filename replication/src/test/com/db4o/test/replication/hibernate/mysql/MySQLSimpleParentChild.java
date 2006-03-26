package com.db4o.test.replication.hibernate.mysql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.impl.HibernateReplicationProviderImpl;
import com.db4o.test.replication.db4o.Db4oReplicationTestUtil;
import com.db4o.test.replication.hibernate.HibernateUtil;
import com.db4o.test.replication.template.SimpleParentChild;

public class MySQLSimpleParentChild extends SimpleParentChild {
	protected void initproviderPairs() {
		TestableReplicationProviderInside a;
		TestableReplicationProviderInside b;

		a = new HibernateReplicationProviderImpl(HibernateUtil.produceMySQLConfigA(), "A");
		b = Db4oReplicationTestUtil.newProviderB();
		addProviderPairs(a, b);

		a = Db4oReplicationTestUtil.newProviderB();
		b = new HibernateReplicationProviderImpl(HibernateUtil.produceMySQLConfigA(), "B");
		addProviderPairs(a, b);
	}

	public void test() {
		super.test();
	}
}

package com.db4o.test.replication.hibernate.mysql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.impl.HibernateReplicationProviderImpl;
import com.db4o.test.replication.collections.ListContent;
import com.db4o.test.replication.collections.ListHolder;
import com.db4o.test.replication.collections.ListTest;
import com.db4o.test.replication.hibernate.HibernateUtil;
import org.hibernate.cfg.Configuration;

public class MySQLListTest extends ListTest {
	protected TestableReplicationProviderInside prepareProviderA() {
		Configuration configuration = HibernateUtil.produceMySQLConfigA();
		configuration.addClass(ListHolder.class);
		configuration.addClass(ListContent.class);
		return new HibernateReplicationProviderImpl(configuration, "A");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		Configuration configuration = HibernateUtil.produceMySQLConfigB();
		configuration.addClass(ListHolder.class);
		configuration.addClass(ListContent.class);
		return new HibernateReplicationProviderImpl(configuration, "B");
	}

	public void test() {
		super.test();
	}
}

package com.db4o.test.replication.hibernate.postgresql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.cfg.ReplicationConfiguration;
import com.db4o.replication.hibernate.impl.HibernateReplicationProviderImpl;
import com.db4o.test.replication.db4o.Db4oReplicationTestUtil;
import com.db4o.test.replication.hibernate.HibernateMapTest;
import com.db4o.test.replication.hibernate.HibernateUtil;

public class PostgreSQLMapTest extends HibernateMapTest {
	protected TestableReplicationProviderInside prepareProviderA() {
		return new HibernateReplicationProviderImpl(HibernateUtil.producePostgreSQLConfigA());
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return Db4oReplicationTestUtil.newProviderB();
	}

	public void test() {
		cfg = ReplicationConfiguration.decorate(HibernateUtil.producePostgreSQLConfigA());
		super.test();
	}
}

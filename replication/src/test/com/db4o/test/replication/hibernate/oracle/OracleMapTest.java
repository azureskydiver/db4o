package com.db4o.test.replication.hibernate.oracle;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.cfg.ReplicationConfiguration;
import com.db4o.replication.hibernate.impl.HibernateReplicationProviderImpl;
import com.db4o.test.replication.db4o.Db4oReplicationTestUtil;
import com.db4o.test.replication.hibernate.HibernateMapTest;
import com.db4o.test.replication.hibernate.HibernateUtil;

public class OracleMapTest extends HibernateMapTest {
	protected TestableReplicationProviderInside prepareProviderA() {
		return new HibernateReplicationProviderImpl(HibernateUtil.produceOracleConfigA());
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return Db4oReplicationTestUtil.newProviderB();
	}

	public void test() {
		cfg = ReplicationConfiguration.decorate(HibernateUtil.produceOracleConfigA());
		super.test();
	}
}

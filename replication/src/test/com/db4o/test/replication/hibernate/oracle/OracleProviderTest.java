package com.db4o.test.replication.hibernate.oracle;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.cfg.ReplicationConfiguration;
import com.db4o.replication.hibernate.impl.HibernateReplicationProviderImpl;
import com.db4o.test.replication.hibernate.HibernateProviderTest;
import com.db4o.test.replication.hibernate.HibernateUtil;

public class OracleProviderTest extends HibernateProviderTest {
	protected void clean() {
		cfg = ReplicationConfiguration.decorate(HibernateUtil.produceOracleConfigA());
		super.clean();
	}

	protected TestableReplicationProviderInside prepareSubject() {
		return new HibernateReplicationProviderImpl(HibernateUtil.produceOracleConfigA());
	}

	public void testReplicationProvider() {
		super.testReplicationProvider();
	}
}

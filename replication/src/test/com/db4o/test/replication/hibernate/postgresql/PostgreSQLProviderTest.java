package com.db4o.test.replication.hibernate.postgresql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.cfg.ReplicationConfiguration;
import com.db4o.replication.hibernate.impl.HibernateReplicationProviderImpl;
import com.db4o.test.replication.hibernate.HibernateProviderTest;
import com.db4o.test.replication.hibernate.HibernateUtil;

public class PostgreSQLProviderTest extends HibernateProviderTest {
	protected void clean() {
		cfg = ReplicationConfiguration.decorate(HibernateUtil.producePostgreSQLConfigA());
		super.clean();
	}

	protected TestableReplicationProviderInside prepareSubject() {
		return new HibernateReplicationProviderImpl(HibernateUtil.producePostgreSQLConfigA());
	}

	public void testReplicationProvider() {
		super.testReplicationProvider();
	}
}

package com.db4o.test.replication.hibernate;

import com.db4o.inside.replication.TestableReplicationProvider;
import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import com.db4o.test.replication.ReplicationAfterDeletionTest;
import com.db4o.test.replication.SPCChild;
import com.db4o.test.replication.SPCParent;
import org.hibernate.cfg.Configuration;

public class HibernateReplicationAfterDeletionTest extends ReplicationAfterDeletionTest {

	protected TestableReplicationProvider prepareProviderA() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(SPCParent.class);
		configuration.addClass(SPCChild.class);
		return new HibernateReplicationProviderImpl(configuration, "A", new byte[]{1});
	}

	protected TestableReplicationProvider prepareProviderB() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(SPCParent.class);
		configuration.addClass(SPCChild.class);
		return new HibernateReplicationProviderImpl(configuration, "B", new byte[]{2});
	}

	public void test() {
		super.test();
	}
}

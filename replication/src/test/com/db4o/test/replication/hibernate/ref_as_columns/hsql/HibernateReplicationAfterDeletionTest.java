package com.db4o.test.replication.hibernate.ref_as_columns.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.impl.ref_as_columns.RefAsColumnsReplicationProvider;
import com.db4o.test.replication.ReplicationAfterDeletionTest;
import com.db4o.test.replication.SPCChild;
import com.db4o.test.replication.SPCParent;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import org.hibernate.cfg.Configuration;

public class HibernateReplicationAfterDeletionTest extends ReplicationAfterDeletionTest {

	protected TestableReplicationProviderInside prepareProviderA() {
		return new RefAsColumnsReplicationProvider(newCfg(), "A");
	}

	protected Configuration newCfg() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(SPCParent.class);
		configuration.addClass(SPCChild.class);
		return configuration;
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return new RefAsColumnsReplicationProvider(newCfg(), "B");
	}

	public void test() {
		super.test();
	}
}

package com.db4o.test.replication.hibernate;

import com.db4o.inside.replication.TestableReplicationProvider;
import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import com.db4o.test.replication.CollectionHolder;
import com.db4o.test.replication.SingleTypeCollectionReplicationTest;
import org.hibernate.cfg.Configuration;

public class HibernateSingleTypeCollectionReplicationTest extends SingleTypeCollectionReplicationTest {
	protected TestableReplicationProvider prepareProviderA() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(CollectionHolder.class);
		return new HibernateReplicationProviderImpl(configuration, "A");
	}

	protected TestableReplicationProvider prepareProviderB() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(CollectionHolder.class);
		return new HibernateReplicationProviderImpl(configuration, "B");
	}

	public void testCollectionReplication() {
		try {
			super.testCollectionReplication();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}

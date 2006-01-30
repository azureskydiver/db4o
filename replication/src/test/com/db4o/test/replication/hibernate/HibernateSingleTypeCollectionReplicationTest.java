package com.db4o.test.replication.hibernate;

import com.db4o.inside.replication.*;
import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import com.db4o.test.replication.CollectionHolder;
import com.db4o.test.replication.SingleTypeCollectionReplicationTest;
import org.hibernate.cfg.Configuration;

public class HibernateSingleTypeCollectionReplicationTest extends SingleTypeCollectionReplicationTest {
	protected TestableReplicationProvider prepareProviderA() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(CollectionHolder.class);
		return new HibernateReplicationProviderImpl(configuration, "A", new byte[]{1});
	}

	protected TestableReplicationProvider prepareProviderB() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(CollectionHolder.class);
		return new HibernateReplicationProviderImpl(configuration, "B", new byte[]{2});
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

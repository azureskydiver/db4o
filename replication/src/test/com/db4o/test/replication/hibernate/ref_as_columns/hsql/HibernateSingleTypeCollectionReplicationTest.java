package com.db4o.test.replication.hibernate.ref_as_columns.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.ref_as_columns.RefAsColumnsReplicationProvider;
import com.db4o.test.replication.CollectionHolder;
import com.db4o.test.replication.SingleTypeCollectionReplicationTest;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import org.hibernate.cfg.Configuration;

public class HibernateSingleTypeCollectionReplicationTest extends SingleTypeCollectionReplicationTest {
	protected TestableReplicationProviderInside prepareProviderA() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(CollectionHolder.class);
		return new RefAsColumnsReplicationProvider(configuration, "A");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(CollectionHolder.class);
		return new RefAsColumnsReplicationProvider(configuration, "B");
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

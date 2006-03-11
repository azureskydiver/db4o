package com.db4o.test.replication.hibernate.ref_as_columns.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.ref_as_columns.RefAsColumnsReplicationProvider;
import com.db4o.test.replication.CollectionHolder;
import com.db4o.test.replication.SingleTypeCollectionReplicationTest;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import org.hibernate.cfg.Configuration;

public class HibernateSingleTypeCollectionReplicationTest extends SingleTypeCollectionReplicationTest {
	protected TestableReplicationProviderInside prepareProviderA() {
		return new RefAsColumnsReplicationProvider(newCfg(), "A");
	}

	protected Configuration newCfg() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(CollectionHolder.class);
		return configuration;
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return new RefAsColumnsReplicationProvider(newCfg(), "B");
	}

	public void testCollectionReplication() {
		super.testCollectionReplication();
	}

	protected void cleanUp() {
		//do nothing
	}
}

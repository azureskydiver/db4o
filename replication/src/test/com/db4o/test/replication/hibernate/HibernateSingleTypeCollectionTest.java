package com.db4o.test.replication.hibernate;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.impl.ref_as_columns.RefAsColumnsReplicationProvider;
import com.db4o.test.replication.CollectionHolder;
import com.db4o.test.replication.SingleTypeCollectionReplicationTest;
import org.hibernate.cfg.Configuration;

public abstract class HibernateSingleTypeCollectionTest extends SingleTypeCollectionReplicationTest {
	protected TestableReplicationProviderInside prepareProviderA() {
		return new RefAsColumnsReplicationProvider(newCfg(), "A");
	}

	protected Configuration newCfg() {
		Configuration configuration = HibernateUtil.createNewDbConfig();
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

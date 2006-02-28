package com.db4o.test.replication.hibernate;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import com.db4o.test.replication.ArrayHolder;
import com.db4o.test.replication.ArrayReplicationTest;
import org.hibernate.cfg.Configuration;

public class HibernateArrayReplicationTest extends ArrayReplicationTest {

	protected TestableReplicationProviderInside prepareProviderA() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(ArrayHolder.class);
		return new HibernateReplicationProviderImpl(configuration, "A");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(ArrayHolder.class);
		return new HibernateReplicationProviderImpl(configuration, "B");
	}

	public void testArrayReplication() {
		throw new UnsupportedOperationException("Hibernate does not support multi dimensional arrays");
	}
}

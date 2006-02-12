package com.db4o.test.replication.hibernate;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import com.db4o.test.replication.collections.ListContent;
import com.db4o.test.replication.collections.ListHolder;
import com.db4o.test.replication.provider.Car;
import com.db4o.test.replication.provider.Pilot;
import com.db4o.test.replication.provider.ReplicationProviderTest;
import org.hibernate.cfg.Configuration;

public class HibernateReplicationProviderTest extends ReplicationProviderTest {
	public void testReplicationProvider() {
		super.testReplicationProvider();
	}

	protected boolean subjectSupportsRollback() {
		return true;
	}

	protected TestableReplicationProviderInside prepareSubject() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();

		configuration.addClass(Car.class);
		configuration.addClass(Pilot.class);
		configuration.addClass(ListHolder.class);
		configuration.addClass(ListContent.class);

		return new HibernateReplicationProviderImpl(configuration);
	}
}
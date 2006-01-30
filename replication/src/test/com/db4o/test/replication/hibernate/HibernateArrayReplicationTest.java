package com.db4o.test.replication.hibernate;

import com.db4o.inside.replication.*;
import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import com.db4o.test.replication.*;

import org.hibernate.cfg.Configuration;

public class HibernateArrayReplicationTest extends ArrayReplicationTest {
    
	protected TestableReplicationProvider prepareProviderA() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(ArrayHolder.class);
		HibernateReplicationProviderImpl p = new HibernateReplicationProviderImpl(configuration, "A", new byte[]{1});
		return p;
	}

	protected TestableReplicationProvider prepareProviderB() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(ArrayHolder.class);
		HibernateReplicationProviderImpl p = new HibernateReplicationProviderImpl(configuration, "B", new byte[]{2});
		return p;
	}

	public void testArrayReplication() {
		super.testArrayReplication();
	}
}

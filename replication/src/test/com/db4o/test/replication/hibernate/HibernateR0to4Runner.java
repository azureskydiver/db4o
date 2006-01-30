package com.db4o.test.replication.hibernate;

import com.db4o.inside.replication.*;
import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import com.db4o.test.replication.*;

import org.hibernate.cfg.Configuration;

public class HibernateR0to4Runner extends R0to4Runner {
    
	protected TestableReplicationProvider prepareProviderA() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(R0.class);
		HibernateReplicationProviderImpl p = new HibernateReplicationProviderImpl(configuration, "A", new byte[]{1});
		return p;
	}

	protected TestableReplicationProvider prepareProviderB() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(R0.class);
		HibernateReplicationProviderImpl p = new HibernateReplicationProviderImpl(configuration, "B", new byte[]{2});
		return p;
	}

	public void test() {
		super.test();
	}
}

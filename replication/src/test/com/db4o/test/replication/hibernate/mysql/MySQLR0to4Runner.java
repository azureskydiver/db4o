package com.db4o.test.replication.hibernate.mysql;

import com.db4o.inside.replication.TestableReplicationProvider;
import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import com.db4o.test.replication.R0;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import com.db4o.test.replication.hibernate.HibernateR0to4Runner;
import org.hibernate.cfg.Configuration;

public class MySQLR0to4Runner extends HibernateR0to4Runner {

	protected TestableReplicationProvider prepareProviderA() {
		Configuration configuration = HibernateConfigurationFactory.produceMySQLConfigA();
		configuration.addClass(R0.class);
		return new HibernateReplicationProviderImpl(configuration, "A");
	}

	protected TestableReplicationProvider prepareProviderB() {
		Configuration configuration = HibernateConfigurationFactory.produceMySQLConfigB();
		configuration.addClass(R0.class);
		return new HibernateReplicationProviderImpl(configuration, "B");
	}

	public void test() {
		super.test();
	}
}

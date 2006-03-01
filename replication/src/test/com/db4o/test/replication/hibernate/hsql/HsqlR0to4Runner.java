package com.db4o.test.replication.hibernate.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import com.db4o.test.replication.R0;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import com.db4o.test.replication.hibernate.HibernateR0to4Runner;

public class HsqlR0to4Runner extends HibernateR0to4Runner {
	protected TestableReplicationProviderInside prepareProviderA() {
		cfgA = HibernateConfigurationFactory.createNewDbConfig();
		cfgA.addClass(R0.class);
		return new HibernateReplicationProviderImpl(cfgA, "A");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		cfgB = HibernateConfigurationFactory.createNewDbConfig();
		cfgB.addClass(R0.class);
		return new HibernateReplicationProviderImpl(cfgB, "B");
	}

	public void test() {
		super.test();
	}
}

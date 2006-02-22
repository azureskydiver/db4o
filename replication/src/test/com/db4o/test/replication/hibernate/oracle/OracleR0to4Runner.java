package com.db4o.test.replication.hibernate.oracle;

import com.db4o.inside.replication.TestableReplicationProvider;
import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import com.db4o.test.replication.R0;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import com.db4o.test.replication.hibernate.HibernateR0to4Runner;
import org.hibernate.cfg.Configuration;

public class OracleR0to4Runner extends HibernateR0to4Runner {

	protected TestableReplicationProvider prepareProviderA() {
		Configuration configuration = HibernateConfigurationFactory.produceOracleConfigA();
		configuration.addClass(R0.class);
		return new HibernateReplicationProviderImpl(configuration, "A", new byte[]{1});
	}

	protected TestableReplicationProvider prepareProviderB() {
		Configuration configuration = HibernateConfigurationFactory.produceOracleConfigB();
		configuration.addClass(R0.class);
		return new HibernateReplicationProviderImpl(configuration, "B", new byte[]{2});
	}

	public void test() {
		super.test();
	}
}

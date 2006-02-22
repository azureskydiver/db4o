package com.db4o.test.replication.hibernate.oracle;

import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import com.db4o.test.replication.Replicated;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import com.db4o.test.replication.hibernate.HibernateReplicationFeaturesMain;

public class OracleReplicationFeaturesMain extends HibernateReplicationFeaturesMain {
	public OracleReplicationFeaturesMain() {
		cfgA = HibernateConfigurationFactory.produceOracleConfigA();
		cfgA.addClass(Replicated.class);
		pA = new HibernateReplicationProviderImpl(cfgA, "A", new byte[]{1});

		cfgB = HibernateConfigurationFactory.produceOracleConfigB();
		cfgB.addClass(Replicated.class);
		pB = new HibernateReplicationProviderImpl(cfgB, "B", new byte[]{2});
	}

	public void test() {
		super.test();
	}
}

package com.db4o.test.replication.hibernate.ref_as_columns.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.HibernateReplicationProvider;
import com.db4o.replication.hibernate.impl.ref_as_columns.RefAsColumnsReplicationProvider;
import com.db4o.test.replication.Replicated;
import com.db4o.test.replication.ReplicationFeaturesMain;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import org.hibernate.cfg.Configuration;

public class HibernateReplicationFeaturesMain extends ReplicationFeaturesMain {
	protected Configuration cfgA;
	protected Configuration cfgB;
	protected HibernateReplicationProvider pA;
	protected HibernateReplicationProvider pB;


	public HibernateReplicationFeaturesMain() {

	}

	protected TestableReplicationProviderInside prepareProviderA() {
		return pA;
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return pB;
	}

	public void test() {
		cfgA = HibernateConfigurationFactory.createNewDbConfig();
		cfgA.addClass(Replicated.class);
		pA = new RefAsColumnsReplicationProvider(cfgA, "A");

		cfgB = HibernateConfigurationFactory.createNewDbConfig();
		cfgB.addClass(Replicated.class);
		pB = new RefAsColumnsReplicationProvider(cfgB, "B");

		super.test();
	}
}

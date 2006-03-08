package com.db4o.test.replication.hibernate.ref_as_table;

import com.db4o.replication.hibernate.ref_as_table.RefAsTableReplicationProvider;
import com.db4o.test.replication.Replicated;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import org.hibernate.cfg.Configuration;

public class RefAsTableDiffDbReplicationFeaturesMain extends RefAsTableSameDbReplicationFeaturesMain {
	public void test() {
		cfgA = HibernateConfigurationFactory.createNewDbConfig();
		cfgA.addClass(Replicated.class);
		final Configuration refCfgA = HibernateConfigurationFactory.createNewDbConfig();
		pA = new RefAsTableReplicationProvider(cfgA, refCfgA, "A");

		cfgB = HibernateConfigurationFactory.createNewDbConfig();
		cfgB.addClass(Replicated.class);
		final Configuration refCfgB = HibernateConfigurationFactory.createNewDbConfig();
		pB = new RefAsTableReplicationProvider(cfgB, refCfgB, "B");

		super.test();
	}
}

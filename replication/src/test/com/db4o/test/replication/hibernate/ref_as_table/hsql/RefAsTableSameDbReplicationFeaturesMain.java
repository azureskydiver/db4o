package com.db4o.test.replication.hibernate.ref_as_table.hsql;

import com.db4o.replication.hibernate.ref_as_table.RefAsTableReplicationProvider;
import com.db4o.test.replication.Replicated;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import com.db4o.test.replication.hibernate.ref_as_columns.hsql.HibernateReplicationFeaturesMain;

public class RefAsTableSameDbReplicationFeaturesMain extends HibernateReplicationFeaturesMain {
	public void test() {
		cfgA = HibernateConfigurationFactory.createNewDbConfig();
		cfgA.addClass(Replicated.class);
		pA = new RefAsTableReplicationProvider(cfgA, cfgA, "A");

		cfgB = HibernateConfigurationFactory.createNewDbConfig();
		cfgB.addClass(Replicated.class);
		pB = new RefAsTableReplicationProvider(cfgB, cfgB, "B");

		super.test();
	}
}

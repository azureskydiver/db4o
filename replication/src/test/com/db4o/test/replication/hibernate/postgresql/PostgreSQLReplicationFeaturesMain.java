package com.db4o.test.replication.hibernate.postgresql;

import com.db4o.replication.hibernate.ref_as_columns.RefAsColumnsReplicationProvider;
import com.db4o.test.replication.Replicated;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import com.db4o.test.replication.hibernate.HibernateReplicationFeaturesMain;

public class PostgreSQLReplicationFeaturesMain extends HibernateReplicationFeaturesMain {
	public PostgreSQLReplicationFeaturesMain() {

	}

	public void test() {
		cfgA = HibernateConfigurationFactory.producePostgreSQLConfigA();
		cfgA.addClass(Replicated.class);
		pA = new RefAsColumnsReplicationProvider(cfgA, "A");

		cfgB = HibernateConfigurationFactory.producePostgreSQLConfigB();
		cfgB.addClass(Replicated.class);
		pB = new RefAsColumnsReplicationProvider(cfgB, "B");
		super.test();
	}
}

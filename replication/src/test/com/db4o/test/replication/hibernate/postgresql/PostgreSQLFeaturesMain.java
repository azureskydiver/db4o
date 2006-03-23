package com.db4o.test.replication.hibernate.postgresql;

import com.db4o.test.replication.hibernate.HibernateReplicationFeaturesMain;

public class PostgreSQLFeaturesMain extends HibernateReplicationFeaturesMain {
// --------------------------- CONSTRUCTORS ---------------------------

	public PostgreSQLFeaturesMain() {

	}

	public void test() {
//		cfgA = HibernateUtil.producePostgreSQLConfigA();
//		cfgA.addClass(Replicated.class);
//		pA = new RefAsColumnsReplicationProvider(cfgA, "A");
//
//		cfgB = HibernateUtil.producePostgreSQLConfigB();
//		cfgB.addClass(Replicated.class);
//		pB = new RefAsColumnsReplicationProvider(cfgB, "B");
		super.test();
	}
}

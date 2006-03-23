package com.db4o.test.replication.hibernate.mysql;

import com.db4o.test.replication.hibernate.HibernateReplicationFeaturesMain;

public class MySQLFeaturesMain extends HibernateReplicationFeaturesMain {
// --------------------------- CONSTRUCTORS ---------------------------

	public MySQLFeaturesMain() {

	}

	public void test() {
//		cfgA = HibernateUtil.produceMySQLConfigA();
//		cfgA.addClass(Replicated.class);
//		pA = new RefAsColumnsReplicationProvider(cfgA, "A");
//
//		cfgB = HibernateUtil.produceMySQLConfigB();
//		cfgB.addClass(Replicated.class);
//		pB = new RefAsColumnsReplicationProvider(cfgB, "B");
		super.test();
	}
}

package com.db4o.test.replication.hibernate.ref_as_table.mysql;

import com.db4o.test.replication.hibernate.ref_as_table.hsql.FeaturesMainRefAsTable;

public class MySQLFeaturesMain extends FeaturesMainRefAsTable {
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

package com.db4o.test.replication.hibernate.ref_as_columns.mysql;

import com.db4o.test.replication.hibernate.ref_as_columns.hsql.FeaturesMainRefAsColumns;

public class MySQLFeaturesMainRefAsColumns extends FeaturesMainRefAsColumns {
	public MySQLFeaturesMainRefAsColumns() {

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

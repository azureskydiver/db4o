package com.db4o.test.replication.hibernate.ref_as_table.postgresql;

import com.db4o.test.replication.hibernate.ref_as_table.hsql.FeaturesMainRefAsTable;

public class PostgreSQLFeaturesMain extends FeaturesMainRefAsTable {
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

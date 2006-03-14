package com.db4o.test.replication.hibernate.ref_as_columns.postgresql;

import com.db4o.test.replication.hibernate.ref_as_columns.hsql.FeaturesMainRefAsColumns;

public class PostgreSQLFeaturesMainRefAsColumns extends FeaturesMainRefAsColumns {
	public PostgreSQLFeaturesMainRefAsColumns() {

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

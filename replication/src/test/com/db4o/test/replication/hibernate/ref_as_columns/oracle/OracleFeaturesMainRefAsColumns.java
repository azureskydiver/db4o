package com.db4o.test.replication.hibernate.ref_as_columns.oracle;

import com.db4o.test.replication.hibernate.ref_as_columns.hsql.FeaturesMainRefAsColumns;

public class OracleFeaturesMainRefAsColumns extends FeaturesMainRefAsColumns {
	public OracleFeaturesMainRefAsColumns() {

	}

	public void test() {
//		cfgA = HibernateUtil.produceOracleConfigA();
//		cfgA.addClass(Replicated.class);
//		pA = new RefAsColumnsReplicationProvider(cfgA, "A");
//
//		cfgB = HibernateUtil.produceOracleConfigB();
//		cfgB.addClass(Replicated.class);
//		pB = new RefAsColumnsReplicationProvider(cfgB, "B");
		super.test();
	}
}

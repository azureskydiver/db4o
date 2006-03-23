package com.db4o.test.replication.hibernate.ref_as_table.oracle;

import com.db4o.test.replication.hibernate.ref_as_table.hsql.FeaturesMainRefAsTable;

public class OracleFeaturesMain extends FeaturesMainRefAsTable {
	public OracleFeaturesMain() {

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

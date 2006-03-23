package com.db4o.test.replication.hibernate.oracle;

import com.db4o.test.replication.hibernate.HibernateReplicationFeaturesMain;

public class OracleFeaturesMain extends HibernateReplicationFeaturesMain {
// --------------------------- CONSTRUCTORS ---------------------------

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

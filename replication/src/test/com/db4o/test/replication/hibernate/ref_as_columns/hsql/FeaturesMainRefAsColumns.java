package com.db4o.test.replication.hibernate.ref_as_columns.hsql;

import com.db4o.test.replication.hibernate.HibernateReplicationFeaturesMain;
import com.db4o.test.replication.hibernate.HibernateUtil;

public class FeaturesMainRefAsColumns extends HibernateReplicationFeaturesMain {
	public FeaturesMainRefAsColumns() {

	}

	public void test() {
		hA = HibernateUtil.refAsColumnsProviderA();
		hB = HibernateUtil.refAsColumnsProviderB();
		super.test();
	}
}

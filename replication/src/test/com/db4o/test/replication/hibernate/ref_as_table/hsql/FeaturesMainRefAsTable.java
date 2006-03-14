package com.db4o.test.replication.hibernate.ref_as_table.hsql;

import com.db4o.test.replication.hibernate.HibernateReplicationFeaturesMain;
import com.db4o.test.replication.hibernate.HibernateUtil;

public class FeaturesMainRefAsTable extends HibernateReplicationFeaturesMain {
	public void test() {
		hA = HibernateUtil.refAsTableProviderA();
		hB = HibernateUtil.refAsTableProviderB();
		super.test();
	}
}

package com.db4o.test.replication.hibernate.mysql;

import com.db4o.replication.hibernate.impl.HibernateReplicationProviderImpl;
import com.db4o.test.replication.db4o.Db4oReplicationTestUtil;
import com.db4o.test.replication.hibernate.HibernateReplicationFeaturesMain;
import com.db4o.test.replication.hibernate.HibernateUtil;

public class MySQLFeaturesMain extends HibernateReplicationFeaturesMain {
// --------------------------- CONSTRUCTORS ---------------------------

	public MySQLFeaturesMain() {

	}

	public void test() {
		hA = new HibernateReplicationProviderImpl(HibernateUtil.produceMySQLConfigA());
		hB = Db4oReplicationTestUtil.newProviderB();

		super.test();
	}
}

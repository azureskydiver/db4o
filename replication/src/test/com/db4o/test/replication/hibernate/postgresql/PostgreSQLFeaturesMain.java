package com.db4o.test.replication.hibernate.postgresql;

import com.db4o.replication.hibernate.impl.HibernateReplicationProviderImpl;
import com.db4o.test.replication.db4o.Db4oReplicationTestUtil;
import com.db4o.test.replication.hibernate.HibernateReplicationFeaturesMain;
import com.db4o.test.replication.hibernate.HibernateUtil;

public class PostgreSQLFeaturesMain extends HibernateReplicationFeaturesMain {
// --------------------------- CONSTRUCTORS ---------------------------

	public PostgreSQLFeaturesMain() {

	}

	public void test() {
		hA = new HibernateReplicationProviderImpl(HibernateUtil.producePostgreSQLConfigA());
		hB = Db4oReplicationTestUtil.newProviderB();

		super.test();
	}
}

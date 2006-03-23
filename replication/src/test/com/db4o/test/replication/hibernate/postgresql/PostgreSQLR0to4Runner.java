package com.db4o.test.replication.hibernate.postgresql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.impl.HibernateReplicationProviderImpl;
import com.db4o.test.replication.db4o.Db4oReplicationTestUtil;
import com.db4o.test.replication.hibernate.HibernateUtil;
import com.db4o.test.replication.jdk14.R0to4RunnerCombinations;
import org.hibernate.cfg.Configuration;

public class PostgreSQLR0to4Runner extends R0to4RunnerCombinations {
// --------------------------- CONSTRUCTORS ---------------------------

	public PostgreSQLR0to4Runner() {
		super();
	}

	protected void initproviderPairs() {
		TestableReplicationProviderInside a;
		TestableReplicationProviderInside b;

		final Configuration cfgA = HibernateUtil.producePostgreSQLConfigA();
		final Configuration cfgB = HibernateUtil.producePostgreSQLConfigB();

		a = new HibernateReplicationProviderImpl(cfgA, "PostgreSQL RefAsColumns");
		b = new HibernateReplicationProviderImpl(cfgB, "PostgreSQL RefAsColumns");
		addProviderPairs(a, b);

		final TestableReplicationProviderInside db4o = Db4oReplicationTestUtil.newProviderA();
		addProviderPairs(a, db4o);
		addProviderPairs(db4o, a);

		a = new HibernateReplicationProviderImpl(cfgA, "PostgreSQL RefAsTable");
		b = new HibernateReplicationProviderImpl(cfgB, "PostgreSQL RefAsTable");
		addProviderPairs(a, b);
	}

	public void test() {
		super.test();
	}
}

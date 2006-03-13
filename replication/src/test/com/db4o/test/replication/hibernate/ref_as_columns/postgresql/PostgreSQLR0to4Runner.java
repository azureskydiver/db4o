package com.db4o.test.replication.hibernate.ref_as_columns.postgresql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.impl.ref_as_columns.RefAsColumnsReplicationProvider;
import com.db4o.replication.hibernate.impl.ref_as_table.RefAsTableReplicationProvider;
import com.db4o.test.replication.db4o.Db4oReplicationTestUtil;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import com.db4o.test.replication.jdk14.R0to4RunnerCombinations;
import org.hibernate.cfg.Configuration;

public class PostgreSQLR0to4Runner extends R0to4RunnerCombinations {
	public PostgreSQLR0to4Runner() {
		super();
	}

	public void test() {
		super.test();
	}

	protected void initproviderPairs() {
		TestableReplicationProviderInside a;
		TestableReplicationProviderInside b;

		final Configuration cfgA = HibernateConfigurationFactory.producePostgreSQLConfigA();
		final Configuration cfgB = HibernateConfigurationFactory.producePostgreSQLConfigB();

		a = new RefAsColumnsReplicationProvider(cfgA, "PostgreSQL RefAsColumns");
		b = new RefAsColumnsReplicationProvider(cfgB, "PostgreSQL RefAsColumns");
		addProviderPairs(a, b);

		final TestableReplicationProviderInside db4o = Db4oReplicationTestUtil.newProviderA();
		addProviderPairs(a, db4o);
		addProviderPairs(db4o, a);

		a = new RefAsTableReplicationProvider(cfgA, "PostgreSQL RefAsTable");
		b = new RefAsTableReplicationProvider(cfgB, "PostgreSQL RefAsTable");
		addProviderPairs(a, b);
	}
}

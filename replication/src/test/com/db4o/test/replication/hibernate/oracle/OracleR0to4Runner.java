package com.db4o.test.replication.hibernate.oracle;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.impl.HibernateReplicationProviderImpl;
import com.db4o.test.replication.db4o.Db4oReplicationTestUtil;
import com.db4o.test.replication.hibernate.HibernateUtil;
import com.db4o.test.replication.template.r0tor4.R0to4Runner;

public class OracleR0to4Runner extends R0to4Runner {
// --------------------------- CONSTRUCTORS ---------------------------

	public OracleR0to4Runner() {
		super();
	}

	protected void initproviderPairs() {
		TestableReplicationProviderInside a;
		TestableReplicationProviderInside b;

		a = new HibernateReplicationProviderImpl(HibernateUtil.produceOracleConfigA(), "A");
		b = new HibernateReplicationProviderImpl(HibernateUtil.produceOracleConfigB(), "B");
		addProviderPairs(a, b);

		a = new HibernateReplicationProviderImpl(HibernateUtil.produceOracleConfigA(), "A");
		b = Db4oReplicationTestUtil.newProviderB();
		addProviderPairs(a, b);
	}

	public void test() {
		super.test();
	}
}

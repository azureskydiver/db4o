package com.db4o.test.replication.jdk14;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.db4o.Db4oReplicationTestUtil;
import com.db4o.test.replication.hibernate.HibernateUtil;
import com.db4o.test.replication.template.r0tor4.R0to4Runner;

public class R0to4RunnerCombinations extends R0to4Runner {
	public R0to4RunnerCombinations() {
		super();
	}

	public void test() {
		super.test();
	}

	protected void initproviderPairs() {
		TestableReplicationProviderInside a;
		TestableReplicationProviderInside b;

		a = HibernateUtil.refAsTableProviderA();
		b = HibernateUtil.refAsTableProviderB();
		addProviderPairs(a, b);

		a = HibernateUtil.refAsTableProviderA();
		b = Db4oReplicationTestUtil.newProviderA();
		addProviderPairs(a, b);
	}


}

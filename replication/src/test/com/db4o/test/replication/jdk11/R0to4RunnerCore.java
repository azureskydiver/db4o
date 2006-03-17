package com.db4o.test.replication.jdk11;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.db4o.Db4oReplicationTestUtil;
import com.db4o.test.replication.template.r0tor4.R0to4Runner;
import com.db4o.test.replication.transients.TransientReplicationProvider;

public class R0to4RunnerCore extends R0to4Runner {

	protected void initproviderPairs() {
		TestableReplicationProviderInside a;
		TestableReplicationProviderInside b;

		a = new TransientReplicationProvider(new byte[]{1}, "Transient");
		b = new TransientReplicationProvider(new byte[]{1}, "Transient");
		addProviderPairs(a, b);

		a = Db4oReplicationTestUtil.newProviderA();
		b = Db4oReplicationTestUtil.newProviderB();
		addProviderPairs(a, b);

		//Second run
		a = Db4oReplicationTestUtil.newProviderA();
		b = Db4oReplicationTestUtil.newProviderB();
		addProviderPairs(a, b);
	}

	public void test() {
		super.test();
	}

	protected void clean() {
		delete(_providerA);
		delete(_providerB);
	}

	@Override
	protected TestableReplicationProviderInside prepareProviderA() {
		throw new RuntimeException("REVISE");
	}

	@Override
	protected TestableReplicationProviderInside prepareProviderB() {
		throw new RuntimeException("REVISE");
	}
}

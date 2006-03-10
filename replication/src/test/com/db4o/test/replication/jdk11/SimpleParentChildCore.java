package com.db4o.test.replication.jdk11;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.test.Test;
import com.db4o.test.replication.db4o.Db4oReplicationTestUtil;
import com.db4o.test.replication.template.SimpleParentChild;
import com.db4o.test.replication.transients.TransientReplicationProvider;

public class SimpleParentChildCore extends SimpleParentChild {

	protected void initproviderPairs() {
		TestableReplicationProviderInside a;
		TestableReplicationProviderInside b;

		a = new TransientReplicationProvider(new byte[]{1}, "Transient");
		b = new TransientReplicationProvider(new byte[]{1}, "Transient");
		addProviderPairs(a, b);

		a = new Db4oReplicationProvider(Test.objectContainer(), "db4o");
		b = Db4oReplicationTestUtil.providerB();
		addProviderPairs(a, b);

		//Second run
		addProviderPairs(a, b);
	}

	public void test() {
		super.test();
	}
}

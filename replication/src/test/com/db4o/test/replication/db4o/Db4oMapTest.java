package com.db4o.test.replication.db4o;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.test.Test;
import com.db4o.test.replication.collections.map.MapTest;

public class Db4oMapTest extends MapTest {

	protected TestableReplicationProviderInside prepareProviderA() {
		return new Db4oReplicationProvider(Test.objectContainer());
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return Db4oReplicationTestUtil.newProviderB();
	}

	public void test() {
		super.test();
	}

}

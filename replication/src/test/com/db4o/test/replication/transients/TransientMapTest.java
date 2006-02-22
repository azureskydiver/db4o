package com.db4o.test.replication.transients;

import com.db4o.inside.replication.TestableReplicationProvider;
import com.db4o.test.replication.collections.map.MapTest;

public class TransientMapTest extends MapTest {
	protected TestableReplicationProvider prepareProviderA() {
		return new TransientReplicationProvider(new byte[]{0});
	}

	protected TestableReplicationProvider prepareProviderB() {
		return new TransientReplicationProvider(new byte[]{1});
	}

	public void test() {
		super.test();
	}
}

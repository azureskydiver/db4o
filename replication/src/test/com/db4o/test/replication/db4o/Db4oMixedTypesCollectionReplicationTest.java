package com.db4o.test.replication.db4o;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.MixedTypesCollectionReplicationTest;

public class Db4oMixedTypesCollectionReplicationTest extends MixedTypesCollectionReplicationTest {
	protected TestableReplicationProviderInside prepareProviderA() {
		return Db4oReplicationTestUtil.newProviderA();
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return Db4oReplicationTestUtil.newProviderB();
	}

	public void testCollectionReplication() {
		super.testCollectionReplication();
	}
}

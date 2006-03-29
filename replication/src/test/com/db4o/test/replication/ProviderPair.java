package com.db4o.test.replication;

import com.db4o.inside.replication.TestableReplicationProviderInside;

class ProviderPair {
	final TestableReplicationProviderInside _providerA;
	final TestableReplicationProviderInside _providerB;

	ProviderPair(TestableReplicationProviderInside _providerA, TestableReplicationProviderInside _providerB) {
		this._providerA = _providerA;
		this._providerB = _providerB;
	}
}

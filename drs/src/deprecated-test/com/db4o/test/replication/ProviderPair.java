package com.db4o.test.replication;

import com.db4o.drs.inside.TestableReplicationProviderInside;

public class ProviderPair {
	public final TestableReplicationProviderInside _providerA;
	public final TestableReplicationProviderInside _providerB;

	public ProviderPair(TestableReplicationProviderInside _providerA, TestableReplicationProviderInside _providerB) {
		this._providerA = _providerA;
		this._providerB = _providerB;
	}
}

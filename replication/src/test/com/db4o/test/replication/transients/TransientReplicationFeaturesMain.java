package com.db4o.test.replication.transients;

import com.db4o.ObjectSet;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.ReplicationProvider;
import com.db4o.test.replication.ReplicationFeaturesMain;

public class TransientReplicationFeaturesMain extends ReplicationFeaturesMain {
	private TestableReplicationProviderInside a = new TransientReplicationProvider(new byte[]{1}, "A");
	private TestableReplicationProviderInside b = new TransientReplicationProvider(new byte[]{2}, "B");

	protected TestableReplicationProviderInside prepareProviderB() {
		return a;
	}

	protected TestableReplicationProviderInside prepareProviderA() {
		return b;
	}

	ObjectSet getStoredObjects(ReplicationProvider rp, Class aClass) {
		return ((TransientReplicationProvider) rp).getStoredObjects();
	}

	public void test() {
		super.test();
	}
}

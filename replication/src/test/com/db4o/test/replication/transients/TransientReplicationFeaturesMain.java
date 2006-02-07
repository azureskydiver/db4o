package com.db4o.test.replication.transients;

import com.db4o.ObjectSet;
import com.db4o.inside.replication.TestableReplicationProvider;
import com.db4o.replication.ReplicationProvider;
import com.db4o.test.replication.ReplicationFeaturesMain;

public class TransientReplicationFeaturesMain extends ReplicationFeaturesMain {
	private static TransientReplicationProvider a = new TransientReplicationProvider(new byte[]{1}, "A");
	private static TransientReplicationProvider b = new TransientReplicationProvider(new byte[]{2}, "B");

	protected TestableReplicationProvider prepareProviderB() {
		return a;
	}

	protected TestableReplicationProvider prepareProviderA() {
		return b;
	}

	ObjectSet getStoredObjects(ReplicationProvider rp, Class aClass) {
		return ((TransientReplicationProvider) rp).getStoredObjects();
	}

	public void test() {
		super.test();
	}

	protected void clean() {
		a = null;
		b = null;
	}

	protected void cleanUp() {
		//do nothing
	}
}

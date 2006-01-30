package com.db4o.test.replication.transients;

import com.db4o.inside.replication.*;
import com.db4o.replication.*;
import com.db4o.test.replication.ReplicationFeaturesMain;
import com.db4o.test.replication.transients.*;
import com.db4o.ObjectSet;

public class TransientReplicationFeaturesMain extends ReplicationFeaturesMain {
	protected TestableReplicationProvider prepareProviderB() {
		return new TransientReplicationProvider(new byte[]{2},"B");
	}

	protected TestableReplicationProvider prepareProviderA() {
		return new TransientReplicationProvider(new byte[]{1},"A");
	}

	ObjectSet getStoredObjects(ReplicationProvider rp, Class aClass) {
		return ((TransientReplicationProvider) rp).getStoredObjects();
	}

	public void test() {
		super.test();
	}

	protected void cleanUp() {
		//do nothing
	}
}

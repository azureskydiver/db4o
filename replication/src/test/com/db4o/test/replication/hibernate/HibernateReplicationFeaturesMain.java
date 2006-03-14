package com.db4o.test.replication.hibernate;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.ReplicationFeaturesMain;

public class HibernateReplicationFeaturesMain extends ReplicationFeaturesMain {
	protected TestableReplicationProviderInside hA;
	protected TestableReplicationProviderInside hB;

	public HibernateReplicationFeaturesMain() {

	}

	protected TestableReplicationProviderInside prepareProviderA() {
		return hA;
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return hB;
	}
}

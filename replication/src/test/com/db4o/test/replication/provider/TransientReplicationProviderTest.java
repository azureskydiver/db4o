package com.db4o.test.replication.provider;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.transients.TransientReplicationProvider;

public class TransientReplicationProviderTest extends ReplicationProviderTest {

	protected TestableReplicationProviderInside prepareSubject() {
		return new TransientReplicationProvider(new byte[]{1});
	}

	protected void destroySubject() {
		subject = null;
	}

	protected boolean subjectSupportsRollback() {
		return false;
	}

	public void testReplicationProvider() {
		super.testReplicationProvider();
	}
}
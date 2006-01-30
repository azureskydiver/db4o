package com.db4o.test.replication.provider;

import com.db4o.inside.replication.*;
import com.db4o.test.replication.transients.*;

public class TransientReplicationProviderTest extends ReplicationProviderTest {

	protected TestableReplicationProviderInside prepareSubject() {
		return new TransientReplicationProvider(new byte[]{1});
	}

	protected boolean subjectSupportsRollback() {
		return false;
	}

	public void testReplicationProvider() {
		super.testReplicationProvider();
	}
}
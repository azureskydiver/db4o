package com.db4o.test.replication.transients;

import com.db4o.inside.replication.*;
import com.db4o.test.replication.*;

public class TransientArrayReplicationTest extends ArrayReplicationTest {
	protected TestableReplicationProvider prepareProviderA() {
		return new TransientReplicationProvider(new byte[]{0});
	}

	protected TestableReplicationProvider prepareProviderB() {
		return new TransientReplicationProvider(new byte[]{1});
	}

	public void testArrayReplication() {
		super.testArrayReplication();
	}
}

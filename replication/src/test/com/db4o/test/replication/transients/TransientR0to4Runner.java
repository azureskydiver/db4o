/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.transients;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.R0to4Runner;

public class TransientR0to4Runner extends R0to4Runner {

	protected TestableReplicationProviderInside prepareProviderA() {
		return new TransientReplicationProvider(new byte[]{0});
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return new TransientReplicationProvider(new byte[]{1});
	}

	public void test() {
		super.test();
	}
}

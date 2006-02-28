/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.transients;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.SimpleParentChild;


public class TransientSimpleParentChild extends SimpleParentChild {

	protected TestableReplicationProviderInside prepareProviderA() {
		return new TransientReplicationProvider(new byte[]{1}, "A");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return new TransientReplicationProvider(new byte[]{2}, "B");
	}

	public void test() {
		super.test();
	}

}

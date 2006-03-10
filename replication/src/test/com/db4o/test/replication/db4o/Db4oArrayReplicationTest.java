/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.db4o;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.test.Test;
import com.db4o.test.replication.ArrayReplicationTest;


public class Db4oArrayReplicationTest extends ArrayReplicationTest {

	protected TestableReplicationProviderInside prepareProviderA() {
		return new Db4oReplicationProvider(Test.objectContainer());
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return Db4oReplicationTestUtil.newProviderB();
	}

	public void testArrayReplication() {
		super.testArrayReplication();
	}


}

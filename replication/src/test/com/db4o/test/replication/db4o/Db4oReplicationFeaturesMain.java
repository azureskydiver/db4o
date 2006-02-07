/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.db4o;

import com.db4o.ObjectContainer;
import com.db4o.ext.ExtDb4o;
import com.db4o.ext.MemoryFile;
import com.db4o.inside.replication.TestableReplicationProvider;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.test.replication.ReplicationFeaturesMain;

public class Db4oReplicationFeaturesMain extends ReplicationFeaturesMain {
	private static final ObjectContainer cA = ExtDb4o.openMemoryFile(new MemoryFile());
	private static final ObjectContainer cB = ExtDb4o.openMemoryFile(new MemoryFile());

	private static final Db4oReplicationProvider pA = new Db4oReplicationProvider(cA);
	private static final Db4oReplicationProvider pB = new Db4oReplicationProvider(cB);

	protected TestableReplicationProvider prepareProviderA() {
		return pA;
	}

	protected TestableReplicationProvider prepareProviderB() {
		return pB;
	}

	public void test() {
		super.test();
	}

	protected void clean() {
		cA.close();
		cB.close();
	}
}

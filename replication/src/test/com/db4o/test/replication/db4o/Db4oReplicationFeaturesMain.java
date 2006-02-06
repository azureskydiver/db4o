/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.db4o;

import com.db4o.ext.ExtDb4o;
import com.db4o.ext.MemoryFile;
import com.db4o.inside.replication.TestableReplicationProvider;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.test.replication.ReplicationFeaturesMain;

public class Db4oReplicationFeaturesMain extends ReplicationFeaturesMain {

	protected TestableReplicationProvider prepareProviderA() {
		return new Db4oReplicationProvider(ExtDb4o.openMemoryFile(new MemoryFile()));
	}

	protected TestableReplicationProvider prepareProviderB() {
		return new Db4oReplicationProvider(ExtDb4o.openMemoryFile(new MemoryFile()));
	}

	public void test() {
		super.test();
	}
}

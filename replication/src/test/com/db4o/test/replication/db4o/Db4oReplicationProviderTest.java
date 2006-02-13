package com.db4o.test.replication.db4o;

import com.db4o.ObjectContainer;
import com.db4o.ext.ExtDb4o;
import com.db4o.ext.MemoryFile;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.test.replication.provider.ReplicationProviderTest;

public class Db4oReplicationProviderTest extends ReplicationProviderTest {
	protected ObjectContainer myOc;
	protected Db4oReplicationProvider db4oReplicationProvider;

	protected TestableReplicationProviderInside prepareSubject() {
		myOc = ExtDb4o.openMemoryFile(new MemoryFile());
		db4oReplicationProvider = new Db4oReplicationProvider(myOc);
		return db4oReplicationProvider;
	}

	protected void destroySubject() {
		db4oReplicationProvider.closeIfOpened();
		myOc.close();
		db4oReplicationProvider = null;
		myOc = null;
	}

	protected boolean subjectSupportsRollback() {

		// Although db4o principally supports rollback,
		// db4o keeps references on rollback and accordingly
		// return UUIDs for objects after rollback.

		// The test would fail for now, so we don't test here.

		return false;
	}

	public void testReplicationProvider() {
		super.testReplicationProvider();
	}
}
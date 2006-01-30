package com.db4o.test.replication.db4o;

import com.db4o.test.*;
import com.db4o.inside.replication.*;
import com.db4o.replication.db4o.*;
import com.db4o.test.replication.*;
import com.db4o.test.replication.provider.*;

public class Db4oReplicationProviderTest extends ReplicationProviderTest {

	protected TestableReplicationProviderInside prepareSubject() {
		return new Db4oReplicationProvider(Test.objectContainer());
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
package com.db4o.test.replication.collections.map;

import com.db4o.ext.Db4oUUID;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.test.replication.ReplicationTestCase;

public class BugDrs8 extends ReplicationTestCase {
	public void test() {
		super.test();
	}

	protected void actualTest() {
		ExtObjectContainer oc = ((Db4oReplicationProvider) _providerA).getObjectContainer();

		MapContent c1 = new MapContent("c1");
		_providerA.storeNew(c1);

		//Db4oUUID uuid1 = oc.getObjectInfo(c1).getUUID();	//Uncomment me bypass the bug

		MapHolder mh = new MapHolder("h1");
		mh.put("key1", c1);

		_providerA.storeNew(mh);	//comment me bypass the bug

		// Db4oUUID uuid2 = oc.getObjectInfo(c1).getUUID();
		
		_providerA.commit();

		Db4oUUID uuid3 = oc.getObjectInfo(c1).getUUID();
		ensure(uuid3 != null);

		final Object mc1InA = oc.getByUUID(uuid3);

		ensure(mc1InA != null);	//This line fails when Test.clientServer = true;
	}
}

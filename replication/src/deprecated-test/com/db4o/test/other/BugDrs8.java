package com.db4o.test.other;

import com.db4o.ext.Db4oUUID;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.test.replication.db4ounit.DrsTestCase;

import db4ounit.Assert;

public class BugDrs8 extends DrsTestCase {

	public void test() {
		ExtObjectContainer oc = ((Db4oReplicationProvider) a().provider()).getObjectContainer();

		MapContent c1 = new MapContent("c1");
		a().provider().storeNew(c1);	//comment me bypass the bug

		//Db4oUUID uuid1 = oc.getObjectInfo(c1).getUUID();	//Uncomment me bypass the bug

		MapHolder mh = new MapHolder("h1");
		mh.put("key1", c1);

		a().provider().storeNew(mh);	//comment me bypass the bug

		Db4oUUID uuid3 = oc.getObjectInfo(c1).getUUID();
		Assert.isNotNull(uuid3);

		final Object mc1InA = oc.getByUUID(uuid3);

		Assert.isNotNull(mc1InA);	//This line fails when Test.clientServer = true;
	}
}

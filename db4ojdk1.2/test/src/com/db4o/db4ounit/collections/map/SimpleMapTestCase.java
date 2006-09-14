package com.db4o.db4ounit.collections.map;

import com.db4o.Db4o;
import com.db4o.ext.Db4oUUID;

import db4ounit.Assert;
import db4ounit.TestRunner;
import db4ounit.extensions.AbstractDb4oTestCase;
import db4ounit.extensions.Db4oTestSuiteBuilder;
import db4ounit.extensions.fixtures.Db4oClientServer;
import db4ounit.extensions.fixtures.Db4oInMemory;

public class SimpleMapTestCase extends AbstractDb4oTestCase{
	
	protected void configure() {
		Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
	}

	public static void main(String[] args) {
        new TestRunner(
                new Db4oTestSuiteBuilder(
                        new Db4oInMemory(),
                        SimpleMapTestCase.class)).run();
        
        new TestRunner(
                new Db4oTestSuiteBuilder(
                        new Db4oClientServer("Db4oClientServer.yap",0xdb40),
                        SimpleMapTestCase.class)).run();
    }
	
	public void testGetByUUID() {
		MapContent c1 = new MapContent("c1");
		db().set(c1);	//comment me bypass the bug

		//db().getObjectInfo(c1).getUUID();	//Uncomment me bypass the bug

		MapHolder mh = new MapHolder("h1");
		mh.map.put("key1", c1);

		db().set(mh);	//comment me bypass the bug

		Db4oUUID uuid = db().getObjectInfo(c1).getUUID();

		Assert.isNotNull(db().getByUUID(uuid));	//This line fails when Test.clientServer = true;
	}
}

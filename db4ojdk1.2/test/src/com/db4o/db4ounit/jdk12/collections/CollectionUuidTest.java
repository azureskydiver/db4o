package com.db4o.db4ounit.jdk12.collections;

import java.util.ArrayList;

import com.db4o.Db4o;
import com.db4o.db4ounit.jdk12.collections.map.SimpleMapTestCase;

import db4ounit.Assert;
import db4ounit.TestRunner;
import db4ounit.extensions.AbstractDb4oTestCase;
import db4ounit.extensions.Db4oTestSuiteBuilder;
import db4ounit.extensions.fixtures.Db4oClientServer;
import db4ounit.extensions.fixtures.Db4oInMemory;

public class CollectionUuidTest extends AbstractDb4oTestCase {	
	
	protected void configure() {
		Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
	}
	
	public void test() {
		ArrayList list = new ArrayList();
		db().set(list);
		Assert.isNotNull(db().getObjectInfo(list).getUUID());
	}
	
	public static void main(String[] args) {
        new TestRunner(
                new Db4oTestSuiteBuilder(
                        new Db4oInMemory(),
                        CollectionUuidTest.class)).run();
        
        new TestRunner(
                new Db4oTestSuiteBuilder(
                        new Db4oClientServer("Db4oClientServer.yap",0xdb40),
                        CollectionUuidTest.class)).run();
    }
}

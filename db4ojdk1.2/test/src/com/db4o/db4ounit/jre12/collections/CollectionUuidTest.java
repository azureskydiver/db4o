package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import com.db4o.config.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class CollectionUuidTest extends AbstractDb4oTestCase {	
	
	protected void configure(Configuration config) {
		config.generateUUIDs(Integer.MAX_VALUE);
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
                        new Db4oSingleClient(),
                        CollectionUuidTest.class)).run();
    }
}

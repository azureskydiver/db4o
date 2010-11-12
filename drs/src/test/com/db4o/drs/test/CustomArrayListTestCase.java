package com.db4o.drs.test;

import com.db4o.drs.test.data.*;

import db4ounit.*;

public class CustomArrayListTestCase extends DrsTestCase {
	
	public void test() {
		
		NamedList original = new NamedList("foo");
		original.add("bar");
		
		a().provider().storeNew(original);
		a().provider().commit();
		
		replicateAll(a().provider(), b().provider());
		
		NamedList replicated = (NamedList)b().provider().getStoredObjects(NamedList.class).iterator().next();
		Assert.areEqual(original.name(), replicated.name());
		CollectionAssert.areEqual(original, replicated);
	}

}

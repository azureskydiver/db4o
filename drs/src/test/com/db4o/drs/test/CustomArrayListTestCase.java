package com.db4o.drs.test;

import java.util.*;

import db4ounit.*;

public class CustomArrayListTestCase extends DrsTestCase {
	
	public static class NamedList extends DelegatingList {
		
		private String _name;
		
		public NamedList(String name) {
			super(new ArrayList());
			_name = name;
		}
		
		public String name() {
			return _name;
		}
	}
	
	public void test() {
		
		NamedList original = new NamedList("foo");
		original.add("bar");
		
		a().provider().storeNew(original);
		a().provider().commit();
		
		replicateAll(a().provider(), b().provider());
		
		NamedList replicated = (NamedList)b().provider().getStoredObjects(NamedList.class).get(0);
		Assert.areEqual(original.name(), replicated.name());
		CollectionAssert.areEqual(original, replicated);
	}

}

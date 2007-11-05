package com.db4o.db4ounit.jre12.staging;

import java.util.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class LinkedHashMapTestCase extends AbstractDb4oTestCase implements OptOutCS {
	public static void main(String[] args) {
		new LinkedHashMapTestCase().runSolo();
	}

	protected void store() throws Exception {
		LinkedHashMap hashmap = new LinkedHashMap();
		for (int i = 0; i < 42; ++i) {
			hashmap.put(new Integer(i), "hello" + i);
		}
		store(hashmap);
	}

	public void test() throws Exception {
		LinkedHashMap hashmap = (LinkedHashMap) retrieveOnlyInstance(LinkedHashMap.class);
		long oldSize = db().systemInfo().totalSize();
		store(hashmap);
		db().commit();
		long newSize = db().systemInfo().totalSize();
		Assert.areEqual(oldSize, newSize);
	}
}

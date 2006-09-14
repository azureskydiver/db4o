package com.db4o.db4ounit.assorted;

import com.db4o.ext.ObjectNotStorableException;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

public class NakedObjectTestCase extends AbstractDb4oTestCase {
	
	public static class Item {
		public Object field = new Object();
	}
	
	public void testStoreNakedObjects() {
		try {
			db().set(new Item());
			Assert.fail("Naked objects can't be stored");
		} catch (ObjectNotStorableException e) { // expected
		}
	}
}

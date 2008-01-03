/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.assorted;

import com.db4o.ext.ObjectNotStorableException;

import db4ounit.*;
import db4ounit.extensions.AbstractDb4oTestCase;

public class NakedObjectTestCase extends AbstractDb4oTestCase {
	
	public static class Item {
		public Object field = new Object();
	}
	
	public void testStoreNakedObjects() {
		Assert.expect(ObjectNotStorableException.class, new CodeBlock() {
			public void run() {
				db().store(new Item());
			}
		});
	}
}

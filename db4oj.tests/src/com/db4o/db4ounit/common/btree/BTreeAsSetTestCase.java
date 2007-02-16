/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.btree;

/**
 * @exclude
 */
public class BTreeAsSetTestCase extends BTreeTestCaseBase {
	
	public void testAddSameValueFromSameTransaction() {
		add(42);
		add(42);
		assertSingleElement(42);
	}

}

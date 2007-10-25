/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta.mixed;

import com.db4o.db4ounit.common.ta.*;

import db4ounit.*;

/**
 * @exclude
 */
public class NNTTestCase extends ItemTestCaseBase {
	
	public static void main(String[] args) {
		new NNTTestCase().runAll();
	}
	
	protected Object createItem() throws Exception {
		NonTAItem item = new NonTAItem();
		item.child = new NonTAChildItem();
		item.child.item = new TAItem();
		item.child.item.value = 42;
		return item;
	}

	protected void assertRetrievedItem(Object obj) throws Exception {
		NonTAItem item = (NonTAItem) obj;
		Assert.isNotNull(item.child);
		Assert.isNotNull(item.child.item);
		Assert.areEqual(0, item.child.item.value);
	}
	
	protected void assertItemValue(Object obj) throws Exception {
		NonTAItem item = (NonTAItem) obj;
		Assert.areEqual(42, item.child.item.value());
	}

	

}

/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta.mixed;

import com.db4o.db4ounit.common.ta.*;

import db4ounit.*;

/**
 * @exclude
 */
public class TNTTestCase extends ItemTestCaseBase {
	
	public static void main(String[] args) {
		new TNTTestCase().runAll();
	}
	
	protected Object createItem() throws Exception {
		TNTItem item = new TNTItem();
		item.ntItem = new NTItem();
		item.ntItem.tItem = new TItem();
		item.ntItem.tItem.value = 42;
		return item;
	}

	protected void assertRetrievedItem(Object obj) throws Exception {
		TNTItem item = (TNTItem) obj;
		Assert.isNull(item.ntItem);
	}
	
	protected void assertItemValue(Object obj) throws Exception {
		TNTItem item = (TNTItem) obj;
		NTItem ntItem = item.value();
		Assert.isNotNull(ntItem);
		Assert.isNotNull(ntItem.tItem);
		Assert.areEqual(0, ntItem.tItem.value);
		Assert.areEqual(42, ntItem.tItem.value());
	}

}

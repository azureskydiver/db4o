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
		return new NNTItem(42);
	}

	protected void assertRetrievedItem(Object obj) throws Exception {
		NNTItem item = (NNTItem) obj;
		Assert.isNotNull(item.ntItem);
		Assert.isNotNull(item.ntItem.tItem);
		Assert.areEqual(0, item.ntItem.tItem.value);
	}
	
	protected void assertNullItem(Object obj) throws Exception {
		Assert.isNull(((NNTItem) obj).ntItem);
	}
	
	protected void assertItemValue(Object obj) throws Exception {
		NNTItem item = (NNTItem) obj;
		Assert.areEqual(42, item.ntItem.tItem.value());
	}

}

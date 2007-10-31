/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta.mixed;

import com.db4o.db4ounit.common.ta.*;

import db4ounit.*;

/**
 * @exclude
 */
public class NTNTestCase extends ItemTestCaseBase {
	
	public static void main(String[] args) {
		new NTNTestCase().runAll();
	}
	
	protected Object createItem() throws Exception {
		return new NTNItem(42);
	}

	protected void assertRetrievedItem(Object obj) throws Exception {
		NTNItem item = (NTNItem) obj;
		Assert.isNotNull(item.tnItem);
		Assert.isNull(item.tnItem.list);
	}
	
	protected void assertNullItem(Object obj) throws Exception {
		NTNItem item = (NTNItem) obj;
		Assert.isNull(item.tnItem);
	}
	
	protected void assertItemValue(Object obj) throws Exception {
		NTNItem item = (NTNItem) obj;
		Assert.areEqual(LinkedList.newList(42), item.tnItem.value());
	}

}

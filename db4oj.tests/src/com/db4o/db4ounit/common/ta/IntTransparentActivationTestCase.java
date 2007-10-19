/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

/**
 * @exclude
 */
public class IntTransparentActivationTestCase extends TransparentActivationTestCaseBase implements OptOutCS{
	public static void main(String[] args) {
		new IntTransparentActivationTestCase().runAll();
	}

	protected void store() throws Exception {
		TAIntItem item = new TAIntItem();
		item.value = 42;
		item.i = new Integer(1);
		item.obj = new Integer(2);
		item.list = LinkedList.newList(5);
		store(item);
	}

	public void test() throws Exception {
		TAIntItem item = (TAIntItem) retrieveOnlyInstance(TAIntItem.class);
		asertNullItem(item);
		Assert.areEqual(42, item.value());
		Assert.areEqual(new Integer(1), item.integerValue());
		Assert.areEqual(new Integer(2), item.object());
		Assert.isNotNull(item.list());
		Assert.isNull(item.list().next);
	}

	private void asertNullItem(TAIntItem item) {
		Assert.areEqual(0, item.value);
		Assert.areEqual(null, item.i);
		Assert.areEqual(null, item.obj);
		Assert.isNull(item.list);
	}

}

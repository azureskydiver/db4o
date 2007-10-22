/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

/**
 * @exclude
 */
public class StringTransparentActivationTestCase extends TransparentActivationTestCaseBase implements OptOutCS{
	private static final LinkedList LIST = LinkedList.newList(10);

	public static void main(String[] args) {
		new StringTransparentActivationTestCase().runAll();
	}

	protected void store() throws Exception {
		TAStringItem item = new TAStringItem();
		item.value = "42";
		item.obj = "hello";
		item.list = LIST;
		store(item);
	}

	public void test() throws Exception {
		TAStringItem item = (TAStringItem) retrieveOnlyInstance(TAStringItem.class);
		asertNullItem(item);
		Assert.areEqual("42", item.value());
		Assert.areEqual("hello", item.object());
		Assert.areEqual(LIST, item.list());
	}

	private void asertNullItem(TAStringItem item) {
		Assert.isNull(item.value);
		Assert.isNull(item.obj);
		Assert.isNull(item.list);
	}

}

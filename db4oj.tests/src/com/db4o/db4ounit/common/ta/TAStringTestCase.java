/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

/**
 * @exclude
 */
public class TAStringTestCase extends TransparentActivationTestCaseBase implements OptOutCS{

	public static void main(String[] args) {
		new TAStringTestCase().runAll();
	}

	protected void store() throws Exception {
		TAStringItem item = new TAStringItem();
		item.value = "42";
		item.obj = "hello";
		store(item);
	}

	public void test() throws Exception {
		TAStringItem item = (TAStringItem) retrieveOnlyInstance(TAStringItem.class);
		asertNullItem(item);
		Assert.areEqual("42", item.value());
		Assert.areEqual("hello", item.object());
	}

	private void asertNullItem(TAStringItem item) {
		Assert.isNull(item.value);
		Assert.isNull(item.obj);
	}

}

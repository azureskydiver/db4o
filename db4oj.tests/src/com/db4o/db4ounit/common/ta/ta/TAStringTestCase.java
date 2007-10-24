/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta.ta;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

/**
 * @exclude
 */
public class TAStringTestCase extends TAItemTestCaseBase implements OptOutCS{

	public static void main(String[] args) {
		new TAStringTestCase().runAll();
	}

	protected Object createItem() throws Exception {
		TAStringItem item = new TAStringItem();
		item.value = "42";
		item.obj = "hello";
		return item;
	}

	protected void assertItemValue(Object obj) throws Exception {
		TAStringItem item = (TAStringItem) obj;
		Assert.areEqual("42", item.value());
		Assert.areEqual("hello", item.object());
	}

	protected void assertRetrievedItem(Object obj) {
		TAStringItem item = (TAStringItem) obj;
		Assert.isNull(item.value);
		Assert.isNull(item.obj);
	}

}

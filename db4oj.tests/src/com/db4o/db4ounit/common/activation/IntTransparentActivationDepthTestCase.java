/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.activation;

import com.db4o.db4ounit.common.ta.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

/**
 * @exclude
 */
public class IntTransparentActivationDepthTestCase extends TransparentActivationTestCaseBase implements OptOutCS{
	public static void main(String[] args) {
		new IntTransparentActivationDepthTestCase().runAll();
	}

	protected void store() throws Exception {
		TAIntItem item = new TAIntItem();
		item.value = 42;
		item.list = LinkedList.newList(5);
		store(item);
	}

	public void test() throws Exception {
		TAIntItem item = (TAIntItem) retrieveOnlyInstance(TAIntItem.class);
		asertNullItem(item);
		Assert.areEqual(42, item.getValue());
		Assert.isNotNull(item.list);
		Assert.isNull(item.list.next);
	}

	private void asertNullItem(TAIntItem item) {
		Assert.areEqual(0, item.value);
		Assert.isNull(item.list);
	}

}

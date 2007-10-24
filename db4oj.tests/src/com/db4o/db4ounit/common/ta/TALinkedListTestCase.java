/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

/**
 * @exclude
 */
public class TALinkedListTestCase extends TransparentActivationTestCaseBase implements OptOutCS{
	private static final LinkedList LIST = LinkedList.newList(10);

	public static void main(String[] args) {
		new TALinkedListTestCase().runAll();
	}

	protected void store() throws Exception {
		TALinkedListItem item = new TALinkedListItem();
		store(item);
	}

	public void test() throws Exception {
		TALinkedListItem item = (TALinkedListItem) retrieveOnlyInstance(TALinkedListItem.class);
		asertNullItem(item);
		Assert.areEqual(LIST, item.list());
	}

	private void asertNullItem(TALinkedListItem item) {
		Assert.isNull(item.list);
	}

}

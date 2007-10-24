/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta.ta;

import com.db4o.db4ounit.common.ta.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

/**
 * @exclude
 */
public class TALinkedListTestCase extends TAItemTestCaseBase implements OptOutCS{
	private static final LinkedList LIST = LinkedList.newList(10);

	public static void main(String[] args) {
		new TALinkedListTestCase().runAll();
	}

	protected Object createItem() throws Exception {
		TALinkedListItem item = new TALinkedListItem();
		item.list = LIST;
		return item;
	}

	protected void assertItemValue(Object obj) throws Exception {
		TALinkedListItem item = (TALinkedListItem) obj;
		Assert.areEqual(LIST, item.list());
	}

	protected void assertRetrievedItem(Object obj) {
		TALinkedListItem item = (TALinkedListItem) obj;
		Assert.isNull(item.list);
	}

}

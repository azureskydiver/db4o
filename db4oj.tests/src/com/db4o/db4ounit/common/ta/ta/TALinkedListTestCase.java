/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta.ta;

import db4ounit.*;

/**
 * @exclude
 */
public class TALinkedListTestCase extends TAItemTestCaseBase {
    
	private static final TALinkedList LIST = TALinkedList.newList(10);

	public static void main(String[] args) {
		new TALinkedListTestCase().runSolo();
	}

	protected Object createItem() throws Exception {
		TALinkedListItem item = new TALinkedListItem();
		item.list = LIST;
		return item;
	}

	protected void assertItemValue(Object obj) throws Exception {
		TALinkedListItem item = (TALinkedListItem) obj;
		Assert.areEqual(LIST,item.list());
	}


}

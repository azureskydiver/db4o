/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta.nonta;

import com.db4o.db4ounit.common.ta.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

/**
 * @exclude
 */
public class NonTALinkedListTestCase extends NonTAItemTestCaseBase implements OptOutCS{
	private static final LinkedList LIST = LinkedList.newList(10);

	public static void main(String[] args) {
		new NonTALinkedListTestCase().runAll();
	}

    protected void assertItemValue(Object obj) {
        Assert.areEqual(LIST, ((LinkedListItem)obj).list());
    }

    protected Object createItem() {
        LinkedListItem item = new LinkedListItem();
        item.list = LIST;
        return item;
    }

}

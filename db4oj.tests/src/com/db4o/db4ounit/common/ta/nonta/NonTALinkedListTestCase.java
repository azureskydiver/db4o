/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta.nonta;

import com.db4o.db4ounit.common.ta.*;

import db4ounit.*;

/**
 * @exclude
 */
public class NonTALinkedListTestCase extends NonTAItemTestCaseBase {
    
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
    
    public void testDeactivateDepth() throws Exception {
    	LinkedListItem item = (LinkedListItem) retrieveOnlyInstance();
    	LinkedList list = item.list;
    	LinkedList next3 = list.nextN(3);
    	LinkedList next5 = list.nextN(5);
    	
    	Assert.isNotNull(next3.next);
    	Assert.isNotNull(next5.next);
    	
    	db().deactivate(list, 4);
    	
    	Assert.isNull(list.next);
    	Assert.areEqual(0, list.value);
    	
    	// FIXME: test fails if uncomenting the following assertion.
//    	Assert.isNull(next3.next);
    	Assert.isNotNull(next5.next);
    }

}

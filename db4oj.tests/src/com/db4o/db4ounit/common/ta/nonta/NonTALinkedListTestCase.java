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
        Assert.areEqual(LIST, ((LinkedListItem)obj).list);
    }

    protected Object createItem() {
        LinkedListItem item = new LinkedListItem();
        item.list = LIST;
        return item;
    }
    
    public void testDeactivateDepth() throws Exception {
    	final LinkedListItem item = queryItem();
    	final LinkedList list = item.list;
    	final LinkedList next1 = list.nextN(1);
    	final LinkedList next2 = list.nextN(2);
    	final LinkedList next3 = list.nextN(3);
    	final LinkedList next4 = list.nextN(4);
    	final LinkedList next5 = list.nextN(5);
    	
    	Assert.isNotNull(list.next);
    	Assert.isNotNull(next1.next);
    	Assert.isNotNull(next2.next);
    	Assert.isNotNull(next3.next);
    	Assert.isNotNull(next4.next);
    	Assert.isNotNull(next5.next);
    	
    	db().deactivate(list, 4);
    	
    	assertDeactivated(list);
    	assertDeactivated(next1);
    	assertDeactivated(next2);
    	assertDeactivated(next3);
    	assertDeactivated(next4);
    	
    	Assert.isNotNull(next5.next);
    }

	private void assertDeactivated(final LinkedList list) {
		Assert.isNull(list.next);
    	Assert.areEqual(0, list.value);
	}

	private LinkedListItem queryItem() {
		return (LinkedListItem) retrieveOnlyInstance();
	}

}

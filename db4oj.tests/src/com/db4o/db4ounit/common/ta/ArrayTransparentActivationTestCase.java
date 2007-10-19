/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

/**
 * @exclude
 */
public class ArrayTransparentActivationTestCase extends TransparentActivationTestCaseBase implements OptOutCS{
	
	private static final int[] INTS1 = new int[] {1,2,3};
	
	private static final int[] INTS2 = new int[] {4,5,6};

	public static void main(String[] args) {
		new ArrayTransparentActivationTestCase().runAll();
	}

	protected void store() throws Exception {
		TAArrayItem item = new TAArrayItem();
		item.value = INTS1;
		item.obj = INTS2;
		
		item.lists = new LinkedList[] { LinkedList.newList(5), LinkedList.newList(5) };
		item.listsObject = new LinkedList[] { LinkedList.newList(5), LinkedList.newList(5) };
		store(item);
	}

	public void test() throws Exception {
		TAArrayItem item = (TAArrayItem) retrieveOnlyInstance(TAArrayItem.class);
		asertNullItem(item);
		ArrayAssert.areEqual(INTS1, item.value());
		ArrayAssert.areEqual(INTS2, (int[])item.object());
		assertLists(item.lists());
		assertLists((LinkedList[]) item.listsObject());
	}

	private void assertLists(LinkedList[] lists) {
		Assert.areEqual(2, lists.length);
		for(int i = 0; i < lists.length; ++i) {
			assertList(lists[i]);
		}
	}

	private void assertList(LinkedList list) {
		Assert.isNull(list.next);
	}

	private void asertNullItem(TAArrayItem item) {
		Assert.isNull(item.value);
		Assert.isNull(item.obj);
		Assert.isNull(item.lists);
		Assert.isNull(item.listsObject);
	}

}

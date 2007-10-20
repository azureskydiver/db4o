/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

/**
 * @exclude
 */
public class NArrayTransparentActivationTestCase extends TransparentActivationTestCaseBase implements OptOutCS{
	
	private static final int[][] INTS1 = new int[][]{ {1,2,3}, {4,5,6}};
	
	private static final int[][] INTS2 = new int[][] { {4,5,6}, {7,8,9}};
	
	private static final LinkedList[][] LIST1 = new LinkedList[][] {{ LinkedList.newList(5)}, {LinkedList.newList(5)}};
	
	private static final LinkedList[][] LIST2 = new LinkedList[][] {{ LinkedList.newList(5)}, {LinkedList.newList(5)}};

	public static void main(String[] args) {
		new NArrayTransparentActivationTestCase().runAll();
	}

	protected void store() throws Exception {
		TANArrayItem item = new TANArrayItem();
		item.value = INTS1;
		item.obj = INTS2;
		
		item.lists = LIST1;
		item.listsObject = LIST2;
		store(item);
	}

	public void test() throws Exception {
		TANArrayItem item = (TANArrayItem) retrieveOnlyInstance(TANArrayItem.class);
		asertNullItem(item);
		ArrayAssert.areEqual(INTS1, item.value());
		ArrayAssert.areEqual(INTS2, (int[][])item.object());
		ArrayAssert.areEqual(LIST1, item.lists());
		ArrayAssert.areEqual(LIST2, (LinkedList[][]) item.listsObject());
	}

	private void asertNullItem(TANArrayItem item) {
		Assert.isNull(item.value);
		Assert.isNull(item.obj);
		Assert.isNull(item.lists);
		Assert.isNull(item.listsObject);
	}

}

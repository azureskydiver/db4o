/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;

/**
 * @exclude
 */
public class IntArrayListTestCase implements TestCase {
	
	public void testIteratorGoesForwards() {
		IntArrayList list = new IntArrayList();
		assertIterator(new int[] {}, list.intIterator());
		
		list.add(1);
		assertIterator(new int[] { 1 }, list.intIterator());		
		
		list.add(2);
		assertIterator(new int[] { 1, 2 }, list.intIterator());
	}

	private void assertIterator(int[] expected, IntIterator4 iterator) {
		for (int i=0; i<expected.length; ++i) {
			Assert.isTrue(iterator.moveNext());
			Assert.areEqual(expected[i], iterator.currentInt());
			Assert.areEqual(new Integer(expected[i]), iterator.current());
		}
		Assert.isFalse(iterator.moveNext());
	}
	
}

/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.foundation;

import com.db4o.foundation.*;

import db4ounit.*;

/**
 * @exclude
 */
public class SortedCollection4TestCase implements TestCase {
	
	public void testAddAllAndToArray() {
		final Object[] array = IntArrays4.toObjectArray(new int[] { 6, 4, 1, 2, 7, 3 });
		SortedCollection4 collection = new SortedCollection4(new Comparison4() {
			public int compare(Object x, Object y) {
				return ((Integer)x).intValue()-((Integer)y).intValue();
			}
		});	
		Assert.areEqual(0, collection.size());
		collection.addAll(new ArrayIterator4(array));
		Assert.areEqual(array.length, collection.size());
		
		ArrayAssert.areEqual(
				IntArrays4.toObjectArray(new int[] { 1, 2, 3, 4, 6, 7 }),
				collection.toArray(new Object[collection.size()]));
	}
}

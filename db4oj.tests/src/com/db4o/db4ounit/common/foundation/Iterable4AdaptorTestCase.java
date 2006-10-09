/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.TestCase;

/**
 * @exclude
 */
public class Iterable4AdaptorTestCase implements TestCase {
	
	public void testHasNext() {
		new Iterable4Adaptor(newIterable(new int[] { 1, 2, 3 }));
	}

	private Iterable4 newIterable(int[] values) {
		final Collection4 collection = new Collection4();
		collection.addAll(IntArrays4.toObjectArray(values));
		return collection;
	}
}

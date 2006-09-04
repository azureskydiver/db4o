/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.foundation;

import com.db4o.foundation.*;

import db4ounit.*;

/**
 * @exclude
 */
public class IteratorsTestCase implements TestCase {
	
	public void testMap() {
		final int[] array = new int[] { 1, 2, 3 };
		final Collection4 args = new Collection4();
		final Iterator4 iterator = Iterators.map(
			Arrays4.newIterator(array),
			new Function() {
				public Object apply(Object arg) {
					args.add(arg);
					return new Integer(((Integer)arg).intValue()*2);
				}
			}
		);
		Assert.isNotNull(iterator);
		Assert.areEqual(0, args.size());
		
		for (int i=0; i<array.length; ++i) {
			Assert.isTrue(iterator.moveNext());
			Assert.areEqual(i+1, args.size());
			Assert.areEqual(new Integer(array[i]*2), iterator.current());
		}
	}

}

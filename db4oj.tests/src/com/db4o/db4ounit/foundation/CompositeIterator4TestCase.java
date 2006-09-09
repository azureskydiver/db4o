package com.db4o.db4ounit.foundation;

import com.db4o.foundation.*;

import db4ounit.TestCase;

public class CompositeIterator4TestCase implements TestCase {

	public void testWithEmptyIterators() {
		
		Collection4 iterators = new Collection4();
		iterators.add(IntArrays4.newIterator(new int[] { 1, 2, 3 }));
		iterators.add(IntArrays4.newIterator(new int[] { }));
		iterators.add(IntArrays4.newIterator(new int[] { 4 }));
		iterators.add(IntArrays4.newIterator(new int[] { 5, 6 }));
		
		final CompositeIterator4 iterator = new CompositeIterator4(iterators.strictIterator());
		IteratorAssert.areEqual(IntArrays4.newIterator(new int[] { 1, 2, 3, 4, 5, 6 }), iterator);
		
	}

}

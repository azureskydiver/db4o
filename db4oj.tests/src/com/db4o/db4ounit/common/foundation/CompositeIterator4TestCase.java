package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.TestCase;

public class CompositeIterator4TestCase implements TestCase {

	public void testWithEmptyIterators() {		
		assertIterator(newIterator());	
	}
	
	public void testReset() {
		CompositeIterator4 iterator = newIterator();
		assertIterator(iterator);
		iterator.reset();
		assertIterator(iterator);
	}

	private void assertIterator(final CompositeIterator4 iterator) {
		IteratorAssert.areEqual(IntArrays4.newIterator(new int[] { 1, 2, 3, 4, 5, 6 }), iterator);
	}

	private CompositeIterator4 newIterator() {
		Collection4 iterators = new Collection4();
		iterators.add(IntArrays4.newIterator(new int[] { 1, 2, 3 }));
		iterators.add(IntArrays4.newIterator(new int[] { }));
		iterators.add(IntArrays4.newIterator(new int[] { 4 }));
		iterators.add(IntArrays4.newIterator(new int[] { 5, 6 }));
		
		final CompositeIterator4 iterator = new CompositeIterator4(iterators.iterator());
		return iterator;
	}

	
}

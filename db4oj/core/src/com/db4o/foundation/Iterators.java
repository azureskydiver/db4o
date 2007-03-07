/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;


/**
 * Iterator primitives (concat, map, reduce, filter, etc...).
 * 
 * @exclude
 */
public class Iterators {

	public static final Iterator4 EMPTY_ITERATOR = new Iterator4Impl(null);
	
	public static final Iterable4 EMPTY_ITERABLE = new Iterable4() {
		public Iterator4 iterator() {
			return EMPTY_ITERATOR;
		}
	};
	
	static final Object NO_ELEMENT = new Object();
	
	public static Iterator4 concat(Iterator4 iterators) {
		return new CompositeIterator4(iterators);
	}
	
	public static Iterator4 map(Iterator4 iterator, Function4 function) {
		return new FunctionApplicationIterator(iterator, function);
	}
	
	public static Iterator4 map(Object[] array, Function4 function) {
		return map(new ArrayIterator4(array), function);
	}
	
	public static Iterator4 filter(Object[] array, Predicate4 predicate) {
		return filter(new ArrayIterator4(array), predicate);
	}
	
	public static Iterator4 filter(Iterator4 iterator, Predicate4 predicate) {
		return new FilteredIterator(iterator, predicate);
	}

	public static int size(Iterable4 iterable) {
		return size(iterable.iterator());
	}

	private static int size(Iterator4 iterator) {
		int count=0;
		while (iterator.moveNext()) {
			++count;
		}
		return count;
	}
}

/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;

/**
 * Iterator primitives (cat, map, reduce, filter, etc...).
 * 
 * @exclude
 */
public class Iterators {
	
	static final Object NO_ELEMENT = new Object();
	
	public static Iterator4 cat(Iterator4 iterators) {
		return new CompositeIterator4(iterators);
	}
	
	public static Iterator4 map(Iterator4 iterator, Function function) {
		return new MapIterator4(iterator, function);
	}
	
	public static Iterator4 map(Object[] array, Function function) {
		return map(new ArrayIterator4(array), function);
	}	
}

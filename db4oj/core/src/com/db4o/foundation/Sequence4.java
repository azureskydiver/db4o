/* Copyright (C) 2008   Versant Inc.   http://www.db4o.com */

package com.db4o.foundation;

/**
 * @exclude
 */
public interface Sequence4 extends Iterable4 {
	
	boolean add(Object element);
	
	void addAll(Iterable4 iterable);
	
	boolean isEmpty();

	Object get(int index);
	
	int size();
	
	void clear();
	
	boolean remove(Object obj);
	
	boolean contains(Object obj);
	
	Object[] toArray();
	
}

/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.foundation;

public interface Set4 {
	boolean add(Object obj);
	void clear();
	boolean contains(Object obj);
	boolean isEmpty();
	Iterator4 iterator();
	boolean remove(Object obj);
	int size();
}

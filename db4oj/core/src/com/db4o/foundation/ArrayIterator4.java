/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;

public class ArrayIterator4 implements Iterator4 {
	
	Object[] _elements;
	int _next;

	public ArrayIterator4(Object[] elements) {
		_elements = elements;
		_next = -1;
	}

	public boolean moveNext() {
		if (_next < lastIndex()) {
			++_next;
			return true;
		}
		// force exception on unexpected call to current
		_next = _elements.length;
		return false;
	}

	public Object current() {
		return _elements[_next]; 
	}
	
	public void reset() {
		_next = -1;
	}
	
	private int lastIndex() {
		return _elements.length - 1;
	}
}

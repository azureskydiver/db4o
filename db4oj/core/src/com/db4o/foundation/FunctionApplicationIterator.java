/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;

/**
 * @exclude
 */
public class FunctionApplicationIterator implements Iterator4 {

	private final Iterator4 _iterator;
	private final Function4 _function;
	private Object _current;

	public FunctionApplicationIterator(Iterator4 iterator, Function4 function) {
		if (null == iterator) {
			throw new ArgumentNullException();
		}
		if (null == function) {
			throw new ArgumentNullException();
		}
		_iterator = iterator;
		_function = function;
		_current = Iterators.NO_ELEMENT;
	}
	
	public boolean moveNext() {
		if (!_iterator.moveNext()) {
			_current = Iterators.NO_ELEMENT;
			return false;
		}
		_current = _function.apply(_iterator.current());
		return true;
	}

	public Object current() {
		if (Iterators.NO_ELEMENT == _current) {
			throw new IllegalStateException();
		}
		return _current;
	}
}

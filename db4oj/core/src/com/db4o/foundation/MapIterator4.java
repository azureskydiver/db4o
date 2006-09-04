/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;

/**
 * @exclude
 */
public class MapIterator4 implements Iterator4 {

	private final Iterator4 _iterator;
	private final Function _function;
	private Object _current;

	public MapIterator4(Iterator4 iterator, Function function) {
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

	public Object current() {
		if (Iterators.NO_ELEMENT == _current) {
			throw new IllegalStateException();
		}
		return _current;
	}

	public boolean moveNext() {
		if (!_iterator.moveNext()) {
			_current = Iterators.NO_ELEMENT;
			return false;
		}
		_current = _function.apply(_iterator.current());
		return true;
	}

}

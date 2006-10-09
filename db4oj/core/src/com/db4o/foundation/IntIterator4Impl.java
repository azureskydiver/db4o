/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;

/**
 * @exclude
 */
public class IntIterator4Impl implements IntIterator4 {
	
	private final int _count;
	private int[] _content;
	private int _current;
	
	public IntIterator4Impl(int[] content, int count) {
		_content = content;
		_count = count;
		_current = -1;
	}

	public int currentInt() {
		if (_current < 0 || _content == null) {
			throw new IllegalStateException();
		}
		return _content[_current];
	}

	public Object current() {
		return new Integer(currentInt());
	}

	public boolean moveNext() {
		if (++_current >= _count) {
			_content = null;
			return false;
		}
		return true;
	}
}

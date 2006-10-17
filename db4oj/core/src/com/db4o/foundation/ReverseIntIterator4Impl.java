/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;

/**
 * @exclude
 */
public class ReverseIntIterator4Impl implements IntIterator4 {
	
	private final int _count;
	private int[] _content;
	private int _current;
	
	public ReverseIntIterator4Impl(int[] content, int count) {
		_content = content;
		_count = count;
		_current = count;
	}

	public int currentInt() {
		if (_content == null || _current == _count) {
			throw new IllegalStateException();
		}
		return _content[_current];
	}

	public Object current() {
		return new Integer(currentInt());
	}

	public boolean moveNext() {
		if (_current > 0) {
			--_current;
			return true;
		}
		_content = null;
		return false;
	}
	
	public void reset() {
		_current = _count;
	}
}

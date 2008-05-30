/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */
package com.db4o.db4ounit.common.io;

public class LimitedSizeThrowCondition implements ThrowCondition {

	private long _size;

	public LimitedSizeThrowCondition() {
		this(-1);
	}

	public LimitedSizeThrowCondition(long initialSize) {
		_size = initialSize;
	}
	
	public void size(long size) {
		_size = size;
	}
	
	public boolean shallThrow(long pos, int numBytes) {
		return (_size >= 0) && (pos + numBytes > _size);
	}
}
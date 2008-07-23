/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

/**
 * @exclude
 */
public class AspectVersionContextImpl implements AspectVersionContext{
	
	private final int _aspectCount;

	private  AspectVersionContextImpl(int count) {
		_aspectCount = count;
	}

	public int aspectCount() {
		return _aspectCount;
	}

	public void aspectCount(int count) {
		throw new IllegalStateException();
	}
	
	public static final AspectVersionContextImpl ALWAYS_ENABLED = new AspectVersionContextImpl(Integer.MAX_VALUE);
	
	public static final AspectVersionContextImpl CHECK_ALWAYS_ENABLED = new AspectVersionContextImpl(Integer.MAX_VALUE - 1);
	

}

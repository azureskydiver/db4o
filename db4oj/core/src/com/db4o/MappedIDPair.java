/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o;

/**
 * @exclude
 */
public class MappedIDPair {
	private int _orig;
	private int _mapped;

	public MappedIDPair(int orig, int mapped) {
		_orig = orig;
		_mapped = mapped;
	}
	
	public int orig() {
		return _orig;
	}
	
	public int mapped() {
		return _mapped;
	}
}

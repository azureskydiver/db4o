/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o;

/**
 * @exclude
 */
public class MappedIDPair {
	private int _orig;
	private int _mapped;
	private boolean _seen;

	public MappedIDPair(int orig, int mapped) {
		this(orig,mapped,false);
	}

	public MappedIDPair(int orig, int mapped,boolean seen) {
		_orig = orig;
		_mapped = mapped;
		_seen=seen;
	}

	public int orig() {
		return _orig;
	}
	
	public int mapped() {
		return _mapped;
	}
	
	public boolean seen() {
		return _seen;
	}
	
	public void seen(boolean seen) {
		_seen=seen;
	}
	
	public String toString() {
		return _orig+"->"+_mapped+"("+_seen+")";
	}
}

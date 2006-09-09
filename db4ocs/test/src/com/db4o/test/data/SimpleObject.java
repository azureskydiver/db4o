/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.data;

/*
 * Simple class for test
 */
public class SimpleObject {

	private String _s;

	private int _i;

	public boolean equals(Object obj) {
		if (!(obj instanceof SimpleObject)) {
			return false;
		}
		SimpleObject another = (SimpleObject) obj;
		return _s.equals(another._s) && (_i == another._i);

	}

	public int getI() {
		return _i;
	}

	public void setI(int i) {
		_i = i;
	}

	public SimpleObject(String s, int i) {
		_s = s;
		_i = i;
	}

	public String getS() {
		return _s;
	}

	public void setS(String s) {
		_s = s;
	}
}

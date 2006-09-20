/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.drs.test;

public class ArrayHolder {
    
	public String _name;

	public ArrayHolder[] _array;
    
	public ArrayHolder[][] _arrayN;

	public ArrayHolder() {
	}

	public ArrayHolder(String name) {
		_name = name;
	}

	public String toString() {
		return _name + ", hashcode = " + hashCode();
	}

}

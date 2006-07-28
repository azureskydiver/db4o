/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.aliases;

public class Person2 {

	private String _name;

	public Person2(String name) {
		_name = name;
	}
	
	public String name() {
		return _name;
	}
	
	public boolean equals(Object other) {
		if (other instanceof Person2) {
			return ((Person2)other)._name.equals(_name);
		}
		return false;
	}

}

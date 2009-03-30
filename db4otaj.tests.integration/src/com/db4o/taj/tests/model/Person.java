/* Copyright (C) 2009  db4objects Inc.   http://www.db4o.com */
package com.db4o.taj.tests.model;

public class Person {

	private String _name;

	public Person(String name) {
		_name = name;
	}
	
	public String name() {
		return _name;
	}
	
	public String toString() {
		return _name;
	}

}

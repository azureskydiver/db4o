package com.db4o.test.aliases;

public class Person1 {

	private String _name;

	public Person1(String name) {
		_name = name;
	}
	
	public String name() {
		return _name;
	}

}

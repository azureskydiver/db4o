/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

public class Person {

	private String _name;
	private int _age;
	
	public void setName(String name) {
		this._name = name;
	}
	
	public String getName() {
		return _name;
	}

	public void setAge(int age) {
		this._age = age;
	}

	public int getAge() {
		return _age;
	}
	
	public Person(String name, int age) {
		this._name = name;
		this._age = age;
	}
}

/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.autoinc;

public class TestObject extends CountedObject{
	String name;	
	
	public TestObject(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name+"/"+id;
	}
	
}

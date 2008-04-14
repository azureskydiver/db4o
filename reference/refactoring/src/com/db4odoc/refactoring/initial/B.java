/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */
package com.db4odoc.refactoring.initial;

public class B extends A {
	public int number;
	
	public String toString(){
		return name + "/" + number;
	}
}

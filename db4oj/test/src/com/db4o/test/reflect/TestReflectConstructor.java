/*
 * (c) Copyright 2002 http://www.db4o.com
 * All Rights Reserved.
 */
package com.db4o.test.reflect;

public class TestReflectConstructor {
	
	// adjust, in case you add further test constructors
	public static final int CONSTRUCTOR_COUNT = 2;
	
	public TestReflectConstructor(String str, int i){
		// this one is called.
		// don't modify
		// the test is looking for the String constructor
		// and uses a String and an int to create an instance
	}
	
	public TestReflectConstructor(int i){
		
	}
}


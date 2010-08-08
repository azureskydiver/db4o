/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.qlin;

import com.db4o.internal.*;
import com.db4o.qlin.*;

import db4ounit.*;

/**
 * not sure about Silverlight yet, let's not risk the build.
 * @sharpen.if !SILVERLIGHT
 */
public class PrototypesTestCase implements TestLifeCycle {
	
	private Prototypes _prototypes; 
	
	public static class Item {
		
		public Item _child;
		
		public String _name;
		
		public String name(){
			return _name;
		}
		
		public Item child(){
			return _child;
		}
		
	}
	
	
	public void testStringField(){
		Item item = prototype(Item.class);
		assertPath(item, item._name, "_name");
	}
	
	public void testStringMethod(){
		Item item = prototype(Item.class);
		assertPath(item, item.name(), "_name");
	}
	
	public void testInstanceField(){
		Item item = prototype(Item.class);
		assertPath(item, item._child, "_child");
	}
	
	public void testInstanceMethod(){
		Item item = prototype(Item.class);
		assertPath(item, item.child(), "_child");
	}
	
	public void testLevel2(){
		Item item = prototype(Item.class);
		print(item, item.child().name());
	}
	
	public void _testBug1(){
		PrototypesTestCase testCase = prototype(PrototypesTestCase.class);
		print(testCase, testCase._prototypes.toString());
		
	}
	
	private <T> void print(T t, Object expression){
		print(_prototypes.backingFieldPath(t.getClass(), expression));
	}

	// Arrays.toString can do this, but it's not available on JDK 1.1
	private void print(String... strings) {
		if(strings == null){
			println("null");
			return;
		}
		if(strings.length == 0){
			println("()");
			return;
		}
		String message = "(" + strings[0];
		if(strings.length == 1){
			println(message + ")");
			return;
		}
		for (int i = 1; i < strings.length; i++) {
			message += ", ";
			message += strings[i]; 
		}
		message += ")";
		println(message);
	}

	private void println(String string) {
		System.out.println(string);
	}

	private <T> void assertPath(T t, Object expression, String... expected) {
		ArrayAssert.areEqual(expected, _prototypes.backingFieldPath(t.getClass(), expression));
	}

	private <T> T prototype(Class<T> clazz) {
		return _prototypes.forClass(clazz);
	}


	public void setUp() throws Exception {
		_prototypes = new Prototypes(Platform4.reflectorForType(Item.class), true);
	}



	public void tearDown() throws Exception {
		
	}
	
}

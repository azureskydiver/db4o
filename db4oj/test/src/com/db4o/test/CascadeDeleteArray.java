/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;

public class CascadeDeleteArray {
	
	public ArrayElem[] elements;
	
	public void configure(){
		Db4o.configure().objectClass(this).cascadeOnDelete(true);
	}
	
	public void storeOne(){
		elements = new ArrayElem[] {
			new ArrayElem("one"),
			new ArrayElem("two"),
			new ArrayElem("three"),
		};
	}
	
	public void testOne(){
		
		Test.ensureOccurrences(ArrayElem.class, 3);
		
		Test.delete(this);
		
		Test.ensureOccurrences(ArrayElem.class, 0);
		
		Test.rollBack();
		
		Test.ensureOccurrences(ArrayElem.class, 3);
//		
//		ExtObjectContainer con = Test.objectContainer();
//		
//		con.deactivate(this, Integer.M)
		
		
		Test.delete(this);
		
		Test.ensureOccurrences(ArrayElem.class, 0);
		
		Test.commit();
		
		Test.ensureOccurrences(ArrayElem.class, 0);
		
	}
	
	
	
	public static class ArrayElem{
		
		String name;
		
		public ArrayElem(){
		}
		
		public ArrayElem(String name){
			this.name = name;
		}
	}
}

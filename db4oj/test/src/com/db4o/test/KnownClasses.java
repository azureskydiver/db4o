package com.db4o.test;

import com.db4o.reflect.*;

public class KnownClasses {
	
	public void test(){
		ReflectClass[] knownClasses = Test.objectContainer().knownClasses();
		for (int i = 0; i < knownClasses.length; i++) {
			System.out.println(knownClasses[i]);
		}
		
		
	}

}

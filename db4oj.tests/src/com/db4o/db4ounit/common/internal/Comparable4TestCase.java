/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.internal;

import com.db4o.foundation.*;
import com.db4o.internal.handlers.*;
import com.db4o.reflect.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class Comparable4TestCase extends AbstractDb4oTestCase implements OptOutCS{

	public static void main(String[] args) {
		new Comparable4TestCase().runSolo();
	}
	
	
	public void testHandlers(){
		assertHandlerComparison(IntHandler.class, new Integer(2), new Integer(4));
		
	}
	
	private void assertHandlerComparison(Class handlerClass, Object smaller, Object greater) {
		// FIXME: Change to TypeHandler4 when medthod is added to interface 
		// TypeHandler4 handler = (TypeHandler4) newInstace(handlerClass);
		
		PrimitiveHandler handler = (PrimitiveHandler) newInstance(handlerClass);
		
		PreparedComparison comparable = handler.newPrepareCompare(smaller);
		Assert.isNotNull(comparable);
		Assert.areEqual(0, comparable.compareTo(smaller));
		Assert.isSmaller(0, comparable.compareTo(greater));
		
		comparable = handler.newPrepareCompare(greater);
		Assert.isNotNull(comparable);
		Assert.areEqual(0, comparable.compareTo(greater));
		Assert.isGreater(0, comparable.compareTo(smaller));
		
		comparable = handler.newPrepareCompare(null);
		Assert.isNotNull(comparable);
		Assert.areEqual(0, comparable.compareTo(null));
		Assert.isSmaller(0, comparable.compareTo(smaller));
		
	}
	
	private Object newInstance (Class clazz){
		ReflectClass classReflector = reflector().forClass(clazz);
		ReflectConstructor[] constructors = classReflector.getDeclaredConstructors();
		for (int i = 0; i < constructors.length; i++) {
			ReflectClass[] parameterTypes = constructors[i].getParameterTypes();
			Object[] args = new Object[parameterTypes.length];
			try {
				return constructors[i].newInstance(args);
			} catch (Exception e) {
				
			} 
		}
		throw new IllegalArgumentException("No usable constructor for Class " + clazz);
	}

}

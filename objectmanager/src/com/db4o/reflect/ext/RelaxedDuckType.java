/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.reflect.ext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * RelaxedDuckType. Implements Duck Typing for Java.  ("If it walks like a duck, 
 * quacks like a duck, it...").  Essentially allows programs to treat
 * objects from separate hierarchies as if they were designed with common
 * interfaces as long as they adhere to common naming conventions.
 * <p>
 * This version is the relaxed DuckType.  If a method in the interface is
 * not present on the underlying object, the proxy simply returns null.
 *
 * @author djo
 */
public class RelaxedDuckType extends DuckType implements InvocationHandler {

	public static Object implement(Class interfaceToImplement, Object object) {
		return Proxy.newProxyInstance(interfaceToImplement.getClassLoader(), 
				new Class[] {interfaceToImplement}, new DuckType(object));
	}
	
	protected RelaxedDuckType(Object object) {
		super(object);
	}
	
	private Object object;
	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
			Method realMethod = objectClass.getMethod(method.getName(), method.getParameterTypes());
			return realMethod.invoke(object, args);
		} catch (NoSuchMethodException e) {
			return null;
		} catch (Throwable t) {
			throw t;
		}
	}

}

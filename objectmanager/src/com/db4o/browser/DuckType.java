/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

/**
 * DuckType. Implements Duck Typing for Java.  ("If it walks like a duck, 
 * quacks like a duck, it...").  Essentially allows programs to treat
 * objects from separate hierarchies as if they were designed with common
 * interfaces as long as they adhere to common naming conventions.
 * <p>
 * This version is the strict DuckType.  All methods present in
 * interfaceToImplement must be present on the target object.
 *
 * @author djo
 */
public class DuckType implements InvocationHandler {
	public static Object implement(Class interfaceToImplement, Object object) {
		return Proxy.newProxyInstance(interfaceToImplement.getClassLoader(), 
				new Class[] {interfaceToImplement}, new DuckType(object));
	}
	
	protected DuckType(Object object) {
		this.object = object;
		this.objectClass = object.getClass();
	}

	protected Object object;
	protected Class objectClass;
	
	private HashMap methodCache = new HashMap();
	
	protected Method getMethodByName(Class clazz, String methodName, Class[] args) throws NoSuchMethodException {
		Method method = (Method) methodCache.get(methodName);
		if (method != null) {
			return method;
		}
		
		method = clazz.getMethod(methodName, args);
		methodCache.put(methodName, method);
		return method;
	}
	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Method realMethod = getMethodByName(objectClass, method.getName(), method.getParameterTypes());
		return realMethod.invoke(object, args);
	}
}

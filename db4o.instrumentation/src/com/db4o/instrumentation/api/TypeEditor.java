/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.instrumentation.api;

/**
 * Cross platform interface for type instrumentation.
 */
public interface TypeEditor {
	
	Class actualType();
	
	TypeLoader loader();
	
	ReferenceProvider references();
	
	void addInterface(Class type);
	
	MethodBuilder newPublicMethod(String methodName, Class returnType, Class[] parameterTypes);
}

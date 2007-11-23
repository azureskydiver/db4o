/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.instrumentation.api;


public interface ReferenceProvider {
	
	MethodRef forMethod(Class declaringType, String methodName, Class[] parameterTypes, Class returnType);

	FieldRef forField(Class declaringType, Class fieldType, String fieldName);
}

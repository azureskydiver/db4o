/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.lang.reflect.*;

import com.db4o.reflect.*;

class CMethod implements IMethod{
	
	private final Method method;
	
	public CMethod(Method method){
		this.method = method;
	}
	
	public Object invoke(Object onObject, Object[] parameters){
		try {
			return method.invoke(onObject, parameters);
		} catch (Exception e) {
			return null;
		} 
	}
}

/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.jdk;

import java.lang.reflect.*;

import com.db4o.reflect.*;

public class JdkMethod implements ReflectMethod{
	
	private final Method method;
    private Reflector reflector;
	
	public JdkMethod(Method method_, Reflector reflector_){
		method = method_;
        reflector = reflector_;
	}
	
	public Object invoke(Object onObject, Object[] parameters){
		try {
			return method.invoke(onObject, parameters);
		} catch (Exception e) {
			return null;
		} 
	}

    public ReflectClass getReturnType() {
        return reflector.forClass(method.getReturnType());
    }
    
}

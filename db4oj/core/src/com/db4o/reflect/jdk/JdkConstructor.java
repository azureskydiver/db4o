/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.jdk;

import java.lang.reflect.*;

import com.db4o.*;
import com.db4o.reflect.*;

/**
 * Reflection implementation for Constructor to map to JDK reflection.
 */
public class JdkConstructor implements ReflectConstructor{
	
	private final Reflector reflector;
	private final Constructor constructor;
	
	public JdkConstructor(Reflector reflector_, Constructor constructor_){
		reflector = reflector_;
		constructor = constructor_;
	}
	
	public ReflectClass[] getParameterTypes(){
		return JdkReflector.toMeta(reflector, constructor.getParameterTypes());
	}
	
	public void setAccessible(){
		Platform4.setAccessible(constructor);
	}
	
	public Object newInstance(Object[] parameters){
		try {
            Object obj = constructor.newInstance(parameters);
            if(DTrace.enabled){
                DTrace.NEW_INSTANCE.log(System.identityHashCode(obj));
            }
            return obj;
		} catch (Exception e) {
			return null;
		} 
	}
}

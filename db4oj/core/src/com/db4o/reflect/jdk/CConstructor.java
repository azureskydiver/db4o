/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.jdk;

import java.lang.reflect.*;

import com.db4o.DTrace;
import com.db4o.Platform;
import com.db4o.reflect.*;

/**
 * Reflection implementation for Constructor to map to JDK reflection.
 */
public class CConstructor implements IConstructor{
	
	private final IReflect reflector;
	private final Constructor constructor;
	
	public CConstructor(IReflect reflector, Constructor constructor){
		this.reflector = reflector;
		this.constructor = constructor;
	}
	
	public IClass[] getParameterTypes(){
		return CReflect.toMeta(reflector, constructor.getParameterTypes());
	}
	
	public void setAccessible(){
		Platform.setAccessible(constructor);
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

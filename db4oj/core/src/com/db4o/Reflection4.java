/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.lang.reflect.*;

/**
 * @exclude
 */
public class Reflection4 {

	static Object invoke (Object obj, String methodName, Class[] paramClasses, Object[] params){
		return invoke(obj.getClass().getName(), methodName, paramClasses, params, obj );
	}
	
	static Object invoke (String className, String methodName, Class[] paramClasses, Object[] params, Object onObject){
	    try {
			    Method method = getMethod(className, methodName, paramClasses);
				return method.invoke(onObject, params);
			} catch (Throwable t) {
			}
		return null;
	}

    // calling this "method" will break C# conversion with the old converter
    static Method getMethod(String className, String methodName, Class[] paramClasses) {
	    try {
	        Class clazz = Class.forName(className);
	        Method method = clazz.getMethod(methodName, paramClasses);
	        return method;
		} catch (Throwable t) {
		}
		return null;
    }
	
	 

}

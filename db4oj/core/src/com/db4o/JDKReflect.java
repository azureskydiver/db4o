/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.lang.reflect.*;

/**
 * 
 * package and class name are hard-referenced in JavaOnly#jdk()
 * 
 * TODO: may need to use this on instead of JDK on .NET. Check!
 * 
 */
class JDKReflect extends JDK {
    
    Class constructorClass(){
        return Constructor.class;
    }
	
    /**
     * use for system classes only, since not ClassLoader
     * or Reflector-aware
     */
    final boolean methodIsAvailable(
        String className,
        String methodName,
        Class[] params) {
    	
        try {
        	
            Class clazz = Class.forName(className);
            if (clazz.getMethod(methodName, params) !=null) {
                return true;
            }
            return false;
        } catch (Throwable t) {
        }
        return false;
    }
    
    /**
     * use for system classes only, since not ClassLoader
     * or Reflector-aware
     */
    protected Object invoke (Object obj, String methodName, Class[] paramClasses, Object[] params){
        return invoke(obj.getClass().getName(), methodName, paramClasses, params, obj );
    }
    
    /**
     * use for system classes only, since not ClassLoader
     * or Reflector-aware
     */
    protected Object invoke (String className, String methodName, Class[] paramClasses, Object[] params, Object onObject){
        try {
                Method method = getMethod(className, methodName, paramClasses);
                return method.invoke(onObject, params);
            } catch (Throwable t) {
            }
        return null;
    }

    /**
     * calling this "method" will break C# conversion with the old converter
     * 
     * use for system classes only, since not ClassLoader
     * or Reflector-aware
     */
    protected Method getMethod(String className, String methodName, Class[] paramClasses) {
        try {
            Class clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName, paramClasses);
            return method;
        } catch (Throwable t) {
        }
        return null;
    }


}

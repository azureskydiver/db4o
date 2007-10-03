/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import java.lang.reflect.*;

/**
 * @exclude
 * 
 * Use the methods in this class for system classes only, since they 
 * are not ClassLoader or Reflector-aware.
 * 
 * TODO: this class should go to foundation.reflect, along with ReflectException and ReflectPlatform
 */
public class Reflection4 {
    
    public static Object invoke (Object obj, String methodName) throws ReflectException {
        return invoke(obj.getClass().getName(), methodName, null, null, obj );
    }
    
    public static Object invoke (Object obj, String methodName, Object[] params) throws ReflectException {
        Class[] paramClasses = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            paramClasses[i] = params[i].getClass();
        }
        return invoke(obj.getClass().getName(), methodName, paramClasses, params, obj );
    }

    public static Object invoke (Object obj, String methodName, Class[] paramClasses, Object[] params) throws ReflectException {
        return invoke(obj.getClass().getName(), methodName, paramClasses, params, obj );
    }
    
    public static Object invoke (Class clazz, String methodName, Class[] paramClasses, Object[] params) throws ReflectException {
        return invoke(clazz.getName(), methodName, paramClasses, params, null);
    }
    
    public static Object invoke(String className, String methodName,
            Class[] paramClasses, Object[] params, Object onObject) throws ReflectException {
        Method method = getMethod(className, methodName, paramClasses);
        return invoke(params, onObject, method);
    }
    
    public static Object invoke(Object[] params, Object onObject, Method method) throws ReflectException {
        if(method == null) {
            return null;
        }
        try {
            return method.invoke(onObject, params);
        } catch (InvocationTargetException e) {
            throw new ReflectException(e.getTargetException());
        } catch (IllegalArgumentException e) {
            throw new ReflectException(e);
        } catch (IllegalAccessException e) {
            throw new ReflectException(e);          
        } 
    }

    /**
     * calling this method "method" will break C# conversion with the old converter
     */
    public static Method getMethod(String className, String methodName,
            Class[] paramClasses) {
        Class clazz = ReflectPlatform.forName(className);
        if (clazz == null) {
            return null;
        }
        try {
            return clazz.getMethod(methodName, paramClasses);
        } catch (Exception e) {
            // This is a catch all since the reflection call is
            // intended for cases where the method is not present.
        }
        return null;
    }

}

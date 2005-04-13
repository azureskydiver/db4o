/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.reflect.ext;

import java.lang.reflect.Method;

/**
 * ReflectedMethod.  Encapsulates a method that may or may not exist on 
 * some receiver.  Invocation policy is that if the method can be invoked,
 * it is.  On failure, returns null.
 *
 * @author djo
 */
public class ReflectedMethod {
    
    private Object subject;
    private Method method;
    
    /**
     * Constructor ReflectedMethod.  Create a ReflectedMethod object.
     * 
     * @param subject The object on which the method lives.
     * @param methodName The name of the method.
     * @param paramTypes The method's parameter types.
     */
    public ReflectedMethod(Object subject, String methodName, Class[] paramTypes) {
        this.subject = subject;
        method = null;
        try {
        	method = subject.getClass().getDeclaredMethod(methodName, paramTypes);
        } catch (Exception e) {}
    }
    
    /**
     * Method exists.  Returns true if the underlying method exists, false
     * otherwise.
     * 
     * @return true if the underlying method exists, false otherwise.
     */
    public boolean exists() {
        return method != null;
    }
    
    /**
     * Method invoke.  If possible, invoke the encapsulated method with the
     * specified parameters.
     * 
     * @param params An Object[] containing the parameters to pass.
     * @return any return value or null if there was no return value or an
     * error occured.
     */
    public Object invoke(Object[] params) {
        if (method == null)
            return null;
        try {
        	return method.invoke(subject, params);
        } catch (Exception e) {
            return null;
        }
    }
}



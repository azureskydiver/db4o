/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.query;

import java.io.*;
import java.lang.reflect.*;

import com.db4o.*;
import com.db4o.inside.*;

/**
 * Extend this class and add your #match() method to run native queries.
 * 
 * <br><br><b>! The functionality of this class is not available before db4o version 5.0.
 * It is present in 4.x builds for maintenance purposes only !</b><br><br> 
 *  
 * A class that extends Predicate is required to implement the method
 * #match() following the native query conventions:<br>
 * - The name of the method is "match".<br>
 * - The method is public.<br>
 * - The method returns a boolean.<br>
 * - The method takes one parameter.<br>
 * - The type (Class) of the parameter specifies the extent.<br>
 * - For all instances of the extent that are to be included into the
 * resultset of the query, the method returns true. For all instances
 * that are not to be included the method returns false. <br><br>
 * Here is an example of a #match method that follows these conventions:<br> 
 * <pre><code>
 * public boolean match(Cat cat){<br>
 *     return cat.name.equals("Frizz");<br>
 * }<br>
 * </code></pre><br><br>
 * Native queries for Java JDK5 and above define a #match method in the 
 * abstract Predicate class to ensure these conventions, using generics.
 * Without generics the method is not definable in the Predicate class
 * since alternative method parameter classes would not be possible.
 */
public abstract class Predicate implements Serializable{
    
    private transient Method _matchMethod;
    
    private transient Class _extent;
    
    private transient boolean _failed;
    
    public Predicate(){
        findMatchMethod();
        if(_matchMethod == null){
            Exceptions4.throwRuntimeException(64);
        }
    }
    
    /**
     * public for implementation reasons. Do not call. 
     */
    public final Class getExtent(){
        return _extent;
    }
    
    private void findMatchMethod() {
        Method[] methods=getClass().getMethods();
        for (int methodIdx = 0; methodIdx < methods.length; methodIdx++) {
            Method curMethod=methods[methodIdx];
            // TODO: find a more manageable solution for camel vs Pascal casing
            final String name = curMethod.getName();
			if((name.equals("match") || name.equals("Match")) &&
               curMethod.getReturnType().equals(Boolean.TYPE)) {
                Class[] paramTypes = curMethod.getParameterTypes();
                if(paramTypes != null && paramTypes.length == 1){
                    _extent = paramTypes[0];
                    _matchMethod = curMethod;
                    Platform4.setAccessible(curMethod);
                    return;
                }
            }
        }
        _failed = true;
    
    }
    
    
    /**
     * public for implementation reasons. Do not call. 
     */
    public final boolean invoke(Object obj){
        if(_failed){
            return false;
        }
        try{
            if(_matchMethod == null){
                findMatchMethod();
            }
            if(_matchMethod == null){
                return false;
            }
            Object res = _matchMethod.invoke(this, new Object[]{obj});
            return (((Boolean)res).booleanValue());
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
    
}

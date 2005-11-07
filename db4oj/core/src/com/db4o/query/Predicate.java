/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.query;

import java.io.*;
import java.lang.reflect.*;

import com.db4o.*;

/**
 * Extend this class and add your #match() method to run native queries.
 * <br><br>
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
 *     return cat.name.equals("Occam");<br>
 * }<br>
 * </code></pre><br><br>
 * Native queries for Java JDK5 and above define a #match method in the 
 * abstract Predicate class to ensure these conventions, using generics.
 * Without generics the method is not definable in the Predicate class
 * since alternative method parameter classes would not be possible.
 */
public abstract class Predicate implements Serializable {
	public final static String PREDICATEMETHOD_NAME="match";
	
	static final Class OBJECT_CLASS = Object.class;
	
	private transient Method cachedFilterMethod=null;
	
	// IMPORTANT: must have package visibility because it is used as
	// internal on the .net side
	Method getFilterMethod() {
		if(cachedFilterMethod!=null) {
			return cachedFilterMethod;
		}
		Method[] methods=getClass().getMethods();
		Method untypedMethod=null;
		for (int methodIdx = 0; methodIdx < methods.length; methodIdx++) {
			Method method=methods[methodIdx];
			if (isFilterMethod(method)) {
				if (!OBJECT_CLASS.equals(method.getParameterTypes()[0])) {
					cachedFilterMethod=method;
					return method;
				}
				else {
					untypedMethod=method;
				}
			}
		}
		if(untypedMethod!=null) {
			cachedFilterMethod=untypedMethod;
			return untypedMethod;
		}
		throw new IllegalArgumentException("Invalid predicate.");
	}

	private boolean isFilterMethod(Method method) {
		if (method.getParameterTypes().length != 1) {
			return false;
		}
		if (Deploy.csharp) {
			return method.getName().equalsIgnoreCase(PREDICATEMETHOD_NAME);
		} else {
			return method.getName().equals(PREDICATEMETHOD_NAME);
		}
	}

	public Class extentType() {
		return getFilterMethod().getParameterTypes()[0];
	}

	public boolean appliesTo(Object candidate) {
		try {
			Method filterMethod=getFilterMethod();
			Platform4.setAccessible(filterMethod);
			Object ret=filterMethod.invoke(this,new Object[]{candidate});
			return ((Boolean)ret).booleanValue();
		} catch (Exception e) {
            
            // FIXME: Exceptions should be logged for app developers,
            // but we can't print them out here.
            
			// e.printStackTrace();
            
            
			return false;
		}
	}
}

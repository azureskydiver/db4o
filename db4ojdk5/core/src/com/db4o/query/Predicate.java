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
public abstract class Predicate<ExtentType> implements Serializable{
	public final static String PREDICATEMETHOD_NAME="match";
	
	private transient Method cachedFilterMethod=null;
	
	private Method getFilterMethod() {
		if(cachedFilterMethod!=null) {
			return cachedFilterMethod;
		}
		Method[] methods=getClass().getMethods();
		for (int methodIdx = 0; methodIdx < methods.length; methodIdx++) {
			Method method=methods[methodIdx];
			if((method.getName().equals(PREDICATEMETHOD_NAME))&&method.getParameterTypes().length==1) {					
				String targetName=method.getParameterTypes()[0].getName();
				if(!"java.lang.Object".equals(targetName)) {
					cachedFilterMethod=method;
					return method;
				}
			}
		}

		return null;
	}

	public Class<ExtentType> extentType() {
		return (Class<ExtentType>)getFilterMethod().getParameterTypes()[0];
	}

	public abstract boolean match(ExtentType candidate);
	
	public boolean appliesTo(ExtentType candidate) {
		try {
			Method filterMethod=getFilterMethod();
			filterMethod.setAccessible(true);
			Object ret=filterMethod.invoke(this,new Object[]{candidate});
			return ((Boolean)ret).booleanValue();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}

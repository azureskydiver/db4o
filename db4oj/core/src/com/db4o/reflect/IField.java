/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect;

/** 
 * representation for java.lang.reflect.Field.
 * <br><br>See the respective documentation in the JDK API.
 * @see IReflect
 */
public interface IField {
	
	public Object get(Object onObject);
	
	public String getName();
	
	public IClass getType();
	
	public boolean isPublic();
	
	public boolean isStatic();
	
	public boolean isTransient();
	
	public void set(Object onObject, Object attribute);
	
	public void setAccessible();
	
}

/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect;

/** 
 * representation for java.lang.reflect.Array.
 * <br><br>See the respective documentation in the JDK API.
 * @see IReflect
 */
public interface IArray {
	
	public Object get(Object onArray, int index);
	
	public int getLength(Object array);
	
	public Object newInstance(IClass componentType, int length);
	
	public Object newInstance(IClass componentType, int[] dimensions);
	
	public void set(Object onArray, int index, Object element);
	
}


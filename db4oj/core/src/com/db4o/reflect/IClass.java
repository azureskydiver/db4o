/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect;

/** 
 * representation for java.lang.Class.
 * <br><br>See the respective documentation in the JDK API.
 * @see IReflect
 */
public interface IClass {
	
	public IConstructor[] getDeclaredConstructors();
	
	public IField[] getDeclaredFields();
	
	public IField getDeclaredField(String name);
	
	public IMethod getMethod(String methodName, IClass[] paramClasses);
	
	public boolean isAbstract();
	
	public boolean isInterface();
	
	public Object newInstance();
	
	
}


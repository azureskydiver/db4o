/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect;

import com.db4o.YapReader;


/** 
 * representation for java.lang.Class.
 * <br><br>See the respective documentation in the JDK API.
 * @see IReflect
 */
public interface IClass {
	
	public IClass getComponentType();
	
	public IConstructor[] getDeclaredConstructors();
	
	public IField[] getDeclaredFields();
	
	public IField getDeclaredField(String name);
	
	public IMethod getMethod(String methodName, IClass[] paramClasses);
	
	public String getName();
	
	public IClass getSuperclass();
	
	public boolean isAbstract();
	
	public boolean isArray();
	
	public boolean isAssignableFrom(IClass type);
	
	public boolean isInstance(Object obj);
	
	public boolean isInterface();
	
	public boolean isPrimitive();
	
	public Object newInstance();

    //FIXME: REFLECTOR Big hack to get a runnable version.
    public Class getJavaClass();

	
}


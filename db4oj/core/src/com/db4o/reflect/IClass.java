/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect;

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
	
    public boolean isValueType();
    
	public Object newInstance();
    
    /**
     * instructs to install or deinstall a special constructor for the 
     * respective platform that avoids calling the constructor for the
     * respective class 
     * @param flag true to try to install a special constructor, false if
     * such a constructor is to be removed if present
     * @return true if the special constructor is in place after the call
     */
    public boolean skipConstructor(boolean flag);
    
    public void useConstructor(IConstructor constructor, Object[] params);
	
}

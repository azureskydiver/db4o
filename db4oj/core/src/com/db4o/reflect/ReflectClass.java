/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect;


/** 
 * representation for java.lang.Class.
 * <br><br>See the respective documentation in the JDK API.
 * @see Reflector
 */
public interface ReflectClass {
	
    public ReflectClass getComponentType();
	
	public ReflectField[] getDeclaredFields();
	
	public ReflectField getDeclaredField(String name);
    
	/**
	 * Returns the ReflectClass instance being delegated to.
	 * 
	 * If there's no delegation it should return this. 
	 * 
	 * @return delegate or this
	 */
    public ReflectClass getDelegate();
	
	public ReflectMethod getMethod(String methodName, ReflectClass[] paramClasses);
	
	public String getName();
	
	public ReflectClass getSuperclass();
	
	public boolean isAbstract();
	
	public boolean isArray();
	
	public boolean isAssignableFrom(ReflectClass type);
	
	public boolean isCollection();
	
	public boolean isInstance(Object obj);
	
	public boolean isInterface();
	
	public boolean isPrimitive();
    
    public boolean isSecondClass();
    
	public Object newInstance();
    
    public Reflector reflector();
    	
    // FIXME: remove. Reintroduced since OM depends on it - refactor OM.
	public Object[] toArray(Object obj);

	public Object nullValue();

	public void createConstructor();
	
}

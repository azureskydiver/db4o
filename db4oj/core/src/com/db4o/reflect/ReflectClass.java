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
    
    /**
     * instructs to install or uninstall a special constructor for the 
     * respective platform that avoids calling the constructor for the
     * respective class 
     * @param flag true to try to install a special constructor, false if
     * such a constructor is to be removed if present
     * @param testConstructor true, if the special constructor shall be tested, false if it shall be set without testing
     * @return true if the special constructor is in place after the call
     */
    public boolean skipConstructor(boolean flag, boolean testConstructor);
	
    // FIXME: remove. Reintroduced since OM depends on it - refactor OM.
	public Object[] toArray(Object obj);

	public Object nullValue();

	public void createConstructor(boolean skipConstructor);
	
}

/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect;

/** 
 * representation for java.lang.Class.
 * <br><br>See the respective documentation in the JDK API.
 * @see Reflector
 */
public interface ReflectClass {
	
    public ReflectClass getComponentType();
	
	public ReflectConstructor[] getDeclaredConstructors();
	
	public ReflectField[] getDeclaredFields();
	
	public ReflectField getDeclaredField(String name);
    
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
     * @return true if the special constructor is in place after the call
     */
    public boolean skipConstructor(boolean flag);
	
	public Object[] toArray(Object obj);
    
    public void useConstructor(ReflectConstructor constructor, Object[] params);
    
    /**
     * Method getJdkClass(). Converts a ReflectClass back to its corresponding
     * JDK Class if that class is available.  If that Class is not available,
     * returns java.lang.Object.class.  The JDK Class is available if:
     * <ol>
     * <li>it is on the classpath
     * <li>it is a primitive type
     * </ol>
     * 
     * @return Class the underlying Class object if one is available, otherwise 
     * returns java.lang.Object.class
     */
    public Class getJdkClass();
	
}

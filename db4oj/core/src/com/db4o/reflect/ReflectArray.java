/* Copyright (C) 2004, 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect;


/** 
 * representation for java.lang.reflect.Array.
 * <br><br>See the respective documentation in the JDK API.
 * @see Reflector
 */
public interface ReflectArray {
    
    public int[] dimensions(Object arr);
    
    public int flatten(
        Object a_shaped,
        int[] a_dimensions,
        int a_currentDimension,
        Object[] a_flat,
        int a_flatElement);
	
	public Object get(Object onArray, int index);
	
	/**
	 * Get an object at a particular index from an array
	 * 
	 * @param field A ReflectField pointing to the array inside receiver
	 * @param receiver The object containing a field which is an array (pointed to by field)
	 * @param index The array index to fetch
	 * @return The object at receiver{field}[index]
	 */
	public Object get(ReflectField field, Object receiver, int index);
	
    public ReflectClass getComponentType(ReflectClass a_class);
	
	public int getLength(Object array);
	
	/**
	 * Get the length of an array stored in an object's field
	 * 
	 * @param field A ReflectField pointing to the array inside receiver
	 * @param receiver The object containing a field which is an array (pointed to by field)
	 * @return receiver{field}.length
	 */
	public int getLength(ReflectField field, Object receiver);
    
    public boolean isNDimensional(ReflectClass a_class);
	
	public Object newInstance(ReflectClass componentType, int length);
	
	public Object newInstance(ReflectClass componentType, int[] dimensions);
	
	public void set(Object onArray, int index, Object element);
    
    public int shape(
        Object[] a_flat,
        int a_flatElement,
        Object a_shaped,
        int[] a_dimensions,
        int a_currentDimension);

	
}


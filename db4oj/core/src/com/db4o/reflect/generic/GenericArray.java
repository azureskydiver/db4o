/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.reflect.generic;

import com.db4o.reflect.*;


public class GenericArray implements ReflectArray{
    
    private final GenericReflector _reflector;
    private final ReflectArray _delegate;
    
    public GenericArray(GenericReflector reflector){
        _reflector = reflector;
        _delegate = reflector.getDelegate().array();
    }

    public int[] dimensions(Object arr) {
        return _delegate.dimensions(arr);
    }

    public int flatten(Object a_shaped, int[] a_dimensions, int a_currentDimension, Object[] a_flat, int a_flatElement) {
        return _delegate.flatten(a_shaped, a_dimensions, a_currentDimension, a_flat, a_flatElement);
    }

    public Object get(Object onArray, int index) {
        return _delegate.get(onArray, index);
    }

    public ReflectClass getComponentType(ReflectClass a_class) {
        return _delegate.getComponentType(delegateClass(a_class));
    }

    public int getLength(Object array) {
        return _delegate.getLength(array);
    }
    
    private ReflectClass delegateClass(ReflectClass a_class){
        if(a_class instanceof GenericClass){
        	return ((GenericClass)a_class).getDelegate();
        }
        return a_class;
    }

    public boolean isNDimensional(ReflectClass a_class) {
        return _delegate.isNDimensional(delegateClass(a_class));
    }

    public Object newInstance(ReflectClass componentType, int length) {
        return _delegate.newInstance(delegateClass(componentType), length);
    }

    public Object newInstance(ReflectClass componentType, int[] dimensions) {
        return _delegate.newInstance(delegateClass(componentType), dimensions);
    }

    public void set(Object onArray, int index, Object element) {
        _delegate.set(onArray, index, element);
    }

    public int shape(Object[] a_flat, int a_flatElement, Object a_shaped, int[] a_dimensions, int a_currentDimension) {
        return _delegate.shape(a_flat, a_flatElement, a_shaped, a_dimensions, a_currentDimension);
    }

}

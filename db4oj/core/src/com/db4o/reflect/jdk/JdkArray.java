/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.jdk;

import java.lang.reflect.*;

import com.db4o.*;
import com.db4o.reflect.*;

/**
 * Reflection implementation for Array to map to JDK reflection.
 */
public class JdkArray implements ReflectArray {
    
    private final Reflector _reflector;
    
    JdkArray(Reflector reflector){
        _reflector = reflector;
    }
    
    public int[] dimensions(Object arr){
        int count = 0;
        ReflectClass claxx = _reflector.forObject(arr);
        while (claxx.isArray()) {
            count++;
            claxx = claxx.getComponentType();
        }
        int dim[] = new int[count];
        for (int i = 0; i < count; i++) {
            try {
                dim[i] = getLength(arr);
                arr = get(arr, 0);
            } catch (Exception e) {
                return dim;
            }
        }
        return dim;
    }
    
    public int flatten(
        Object a_shaped,
        int[] a_dimensions,
        int a_currentDimension,
        Object[] a_flat,
        int a_flatElement) {
        if (a_currentDimension == (a_dimensions.length - 1)) {
            for (int i = 0; i < a_dimensions[a_currentDimension]; i++) {
                a_flat[a_flatElement++] = getNoExceptions(a_shaped, i);
            }
        } else {
            for (int i = 0; i < a_dimensions[a_currentDimension]; i++) {
                a_flatElement =
                    flatten(
                        getNoExceptions(a_shaped, i),
                        a_dimensions,
                        a_currentDimension + 1,
                        a_flat,
                        a_flatElement);
            }
        }
        return a_flatElement;
    }
    
    public Object get(Object onArray, int index) {
        return Array.get(onArray, index);
    }
    
    public ReflectClass getComponentType(ReflectClass a_class) {
        while (a_class.isArray()) {
            a_class = a_class.getComponentType();
        }
        return a_class;
    }

    public int getLength(Object array) {
        return Array.getLength(array);
    }

    private final Object getNoExceptions(Object onArray, int index){
        try {
            return get(onArray, index);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isNDimensional(ReflectClass a_class) {
        return a_class.getComponentType().isArray();
    }

    public Object newInstance(ReflectClass componentType, int length) {
        return Array.newInstance(((JdkClass)componentType).getJavaClass(), length);
    }

    public Object newInstance(ReflectClass componentType, int[] dimensions) {
        return Array.newInstance(((JdkClass)componentType).getJavaClass(), dimensions);
    }

    public void set(Object onArray, int index, Object element) {
        Array.set(onArray, index, element);
    }
    
    public int shape(
        Object[] a_flat,
        int a_flatElement,
        Object a_shaped,
        int[] a_dimensions,
        int a_currentDimension) {
        if (a_currentDimension == (a_dimensions.length - 1)) {
            for (int i = 0; i < a_dimensions[a_currentDimension]; i++) {
                set(a_shaped, i, a_flat[a_flatElement++]);
            }
        } else {
            for (int i = 0; i < a_dimensions[a_currentDimension]; i++) {
                a_flatElement =
                    shape(
                        a_flat,
                        a_flatElement,
                        get(a_shaped, i),
                        a_dimensions,
                        a_currentDimension + 1);
            }
        }
        return a_flatElement;
    }


}

/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.reflect.*;

/**
 * Static array tools.
 */
abstract class Array4 {

    static IArray i_reflector;

    public static int[] dimensions(Object a_object) {
        int count = 0;
        Class clazz = a_object.getClass();
        while (clazz.isArray()) {
            count++;
            clazz = clazz.getComponentType();
        }
        int dim[] = new int[count];
        for (int i = 0; i < count; i++) {
            try {
                dim[i] = Array4.reflector().getLength(a_object);
                a_object = Array4.reflector().get(a_object, 0);
            } catch (Exception e) {
                return dim;
            }
        }
        return dim;
    }

    private static final Object element(Object a_array, int a_position) {
        try {
            return reflector().get(a_array, a_position);
        } catch (Exception e) {
            return null;
        }
    }

	public static final int flatten(
        Object a_shaped,
        int[] a_dimensions,
        int a_currentDimension,
        Object[] a_flat,
        int a_flatElement) {
        if (a_currentDimension == (a_dimensions.length - 1)) {
            for (int i = 0; i < a_dimensions[a_currentDimension]; i++) {
            	a_flat[a_flatElement++] = element(a_shaped, i);
            }
        } else {
            for (int i = 0; i < a_dimensions[a_currentDimension]; i++) {
                a_flatElement =
                    flatten(
                        reflector().get(a_shaped, i),
                        a_dimensions,
                        a_currentDimension + 1,
                        a_flat,
                        a_flatElement);
            }
        }
        return a_flatElement;
    }

	public static final IClass getComponentType(IClass a_class) {
        while (a_class.isArray()) {
            a_class = a_class.getComponentType();
        }
        return a_class;
    }

    public static final boolean isNDimensional(IClass a_class) {
        return a_class.getComponentType().isArray();
    }

    //FIXME: This will not allow using per-ObjectContainer reflectors 
    public static final IArray reflector() {
        if (i_reflector == null) {
            i_reflector = ((Config4Impl) Db4o.configure()).reflector().array();
        }
        return i_reflector;
    }

	public static final int shape(
        Object[] a_flat,
        int a_flatElement,
        Object a_shaped,
        int[] a_dimensions,
        int a_currentDimension) {
        if (a_currentDimension == (a_dimensions.length - 1)) {
            for (int i = 0; i < a_dimensions[a_currentDimension]; i++) {
                Array4.reflector().set(a_shaped, i, a_flat[a_flatElement++]);
            }
        } else {
            for (int i = 0; i < a_dimensions[a_currentDimension]; i++) {
                a_flatElement =
                    shape(
                        a_flat,
                        a_flatElement,
                        Array4.reflector().get(a_shaped, i),
                        a_dimensions,
                        a_currentDimension + 1);
            }
        }
        return a_flatElement;
    }

}

/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.lang.reflect.*;
import com.db4o.reflect.*;

/**
 * Reflection implementation for Array to map to JDK reflection.
 */
class CArray implements IArray {

    public Object get(Object onArray, int index) {
        return Array.get(onArray, index);
    }

    public int getLength(Object array) {
        return Array.getLength(array);
    }

    public Object newInstance(Class componentType, int length) {
        return Array.newInstance(componentType, length);
    }

    public Object newInstance(Class componentType, int[] dimensions) {
        return Array.newInstance(componentType, dimensions);
    }

    public void set(Object onArray, int index, Object element) {
        Array.set(onArray, index, element);
    }
}

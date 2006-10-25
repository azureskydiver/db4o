/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.jdk;

import java.lang.reflect.*;

import com.db4o.reflect.*;
import com.db4o.reflect.core.AbstractReflectArray;

/**
 * Reflection implementation for Array to map to JDK reflection.
 */
public class JdkArray extends AbstractReflectArray {
    
    JdkArray(Reflector reflector){
        super(reflector);
    }
    
    public Object newInstance(ReflectClass componentType, int length) {
        return Array.newInstance(JdkReflector.toNative(componentType), length);
    }

    public Object newInstance(ReflectClass componentType, int[] dimensions) {
        return Array.newInstance(JdkReflector.toNative(componentType), dimensions);
    }


}

/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.reflect.*;
import com.db4o.reflect.jdk.CClass;
import com.db4o.reflect.jdk.CReflect;

final class YapConstructor {
	
    private final IClass i_reflectorClass;

    private final EventDispatcher i_eventDispatcher;

    private final IConstructor i_constructor;
    private final Object[] i_params;
    final boolean i_dontCallConstructors;

    YapConstructor(
        YapStream a_stream,
        IClass a_class, 
        IConstructor a_constructor,
        Object[] a_params,
        boolean a_checkDispatcher,
        boolean a_dontCallConstructors) {
        
    	i_reflectorClass = a_class;
        i_constructor = a_constructor;
        i_params = a_params;
        i_dontCallConstructors = a_dontCallConstructors;
	        
        i_eventDispatcher = a_checkDispatcher ? EventDispatcher.forClass(a_stream, i_reflectorClass) : null;
    }

    boolean dispatch(YapStream a_stream, Object obj, int eventID) {
        if (i_eventDispatcher != null) {
            return i_eventDispatcher.dispatch(a_stream, obj, eventID);
        }
        return true;
    }

    String getName() {
        return i_reflectorClass.getName();
    }

    // FIXME: REFLECTOR all callers should call reflectorClass
    Class javaClass() {
        return i_reflectorClass.getJavaClass();
    }

    Object newInstance() throws Exception {
        if (i_constructor == null) {
            return i_reflectorClass.newInstance();
        } else {
            return i_constructor.newInstance(i_params);
        }

    }

    IClass classReflector() {
        return i_reflectorClass;
    }
}

/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.reflect.*;

final class YapConstructor {
    private final Class i_javaClass;
    private IClass i_reflectorClass;

    private final EventDispatcher i_eventDispatcher;

    private final IConstructor i_constructor;
    private final Object[] i_params;
    final boolean i_dontCallConstructors;

    YapConstructor(
        YapStream a_stream,
        Class a_class,
        IConstructor a_constructor,
        Object[] a_params,
        boolean a_checkDispatcher,
        boolean a_dontCallConstructors) {
        
        i_javaClass = a_class;
        i_constructor = a_constructor;
        i_params = a_params;
        i_dontCallConstructors = a_dontCallConstructors;
	        try {
	            if (a_stream == null || a_stream.i_config.i_reflect instanceof CReflect) {
	                i_reflectorClass = new CClass(a_class);
	            } else {
	                i_reflectorClass = a_stream.i_config.i_reflect.forName(a_class.getName());
	            }
	        } catch (ClassNotFoundException e) {
	        }
	        
	        i_eventDispatcher = a_checkDispatcher ? EventDispatcher.forClass(a_stream, i_reflectorClass) : null;
    }

    boolean dispatch(YapStream a_stream, Object obj, int eventID) {
        if (i_eventDispatcher != null) {
            return i_eventDispatcher.dispatch(a_stream, obj, eventID);
        }
        return true;
    }

    String getName() {
        return i_javaClass.getName();
    }

    Class javaClass() {
        return i_javaClass;
    }

    Object newInstance() throws Exception {
        if (i_constructor == null) {
            return i_reflectorClass.newInstance();
        } else {
            return i_constructor.newInstance(i_params);
        }

    }

    IClass reflectorClass() {
        return i_reflectorClass;
    }
}

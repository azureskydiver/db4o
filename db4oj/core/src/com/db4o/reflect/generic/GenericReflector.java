/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.generic;

import com.db4o.*;
import com.db4o.reflect.*;

public class GenericReflector implements Reflector {

    private final Reflector _delegate;
    private final Hashtable4 _dataClassByName = new Hashtable4(1);

    public GenericReflector(Reflector reflector) {
        _delegate = reflector;
    }

    public ReflectArray array() {
        return _delegate.array();
    }

    public int collectionUpdateDepth(ReflectClass claxx) {
        return _delegate.collectionUpdateDepth(claxx);
    }

    public boolean constructorCallsSupported() {
        return false;
    }

    public ReflectClass forClass(Class clazz) {
        return _delegate.forClass(clazz);
    }

    public ReflectClass forName(String className) {
        ReflectClass dataClass = (ReflectClass)_dataClassByName.get(className);
        return dataClass != null ? dataClass : _delegate.forName(className);
    }

    public ReflectClass forObject(Object object) {
    	if (object instanceof GenericObject) return ((GenericObject)object).dataClass();
        return _delegate.forObject(object);
    }

    public boolean isCollection(ReflectClass claxx) {
        return _delegate.isCollection(claxx);
    }

    public void registerCollection(Class clazz) {
        _delegate.registerCollection(clazz);
    }

    public void registerCollectionUpdateDepth(Class clazz, int depth) {
        _delegate.registerCollectionUpdateDepth(clazz, depth);
    }

    public void registerDataClass(GenericClass dataClass) {
        _dataClassByName.put(dataClass.getName(), dataClass);
    }

}

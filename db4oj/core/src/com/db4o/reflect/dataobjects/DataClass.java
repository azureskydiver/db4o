/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.dataobjects;

import com.db4o.reflect.*;

public class DataClass implements IClass {

    private final String _name;

    public DataClass(String name) {
        _name = name;
    }

    public IClass getComponentType() {
        return null;
    }

    public IConstructor[] getDeclaredConstructors() {
        return null;
    }

    public IField[] getDeclaredFields() {
        return new IField[] {};
    }

    public IField getDeclaredField(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public IMethod getMethod(String methodName, IClass[] paramClasses) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getName() {
        return _name;
    }

    public IClass getSuperclass() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isAbstract() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isArray() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isAssignableFrom(IClass type) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isInstance(Object obj) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isInterface() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isPrimitive() {
        // TODO Auto-generated method stub
        return false;
    }

    public Object newInstance() {
        // TODO Auto-generated method stub
        return null;
    }


    //FIXME: REFLECTOR Big hack to get a runnable version.
    public Class getJavaClass() {
        return null;
    }

}

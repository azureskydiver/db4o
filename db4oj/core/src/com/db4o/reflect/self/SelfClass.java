/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.reflect.self;

import com.db4o.reflect.*;


public class SelfClass implements ReflectClass{
    
    private final Class _class;
    
    public SelfClass(Class clazz) {
        _class = clazz;
    }

    public ReflectClass getComponentType() {
        // TODO Auto-generated method stub
        return null;
    }

    public ReflectConstructor[] getDeclaredConstructors() {
        // TODO Auto-generated method stub
        return null;
    }

    public ReflectField[] getDeclaredFields() {
        // TODO Auto-generated method stub
        return null;
    }

    public ReflectField getDeclaredField(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public ReflectClass getDelegate() {
        // TODO Auto-generated method stub
        return null;
    }

    public ReflectMethod getMethod(String methodName, ReflectClass[] paramClasses) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getName() {
        return _class.getName();
    }

    public ReflectClass getSuperclass() {
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

    public boolean isAssignableFrom(ReflectClass type) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isCollection() {
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

    public boolean isSecondClass() {
        // TODO Auto-generated method stub
        return false;
    }

    public Object newInstance() {
        // TODO Auto-generated method stub
        return null;
    }

    public Reflector reflector() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean skipConstructor(boolean flag) {
        // TODO Auto-generated method stub
        return false;
    }

    public void useConstructor(ReflectConstructor constructor, Object[] params) {
        // TODO Auto-generated method stub
        
    }

    public Object[] toArray(Object obj) {
        // TODO Auto-generated method stub
        return null;
    }

}

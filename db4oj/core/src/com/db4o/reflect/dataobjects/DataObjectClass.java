package com.db4o.reflect.dataobjects;

import com.db4o.reflect.*;

public class DataObjectClass implements IClass {

    DataObjectClass(String className) {
    }

    public IClass getComponentType() {
        // TODO Auto-generated method stub
        return null;
    }

    public IConstructor[] getDeclaredConstructors() {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
        return null;
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

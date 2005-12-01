/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.reflect.self;

import com.db4o.reflect.*;


public class SelfReflector implements Reflector{

    public ReflectArray array() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean constructorCallsSupported() {
        // TODO Auto-generated method stub
        return false;
    }

    public ReflectClass forClass(Class clazz) {
        return new SelfClass(clazz);
    }

    public ReflectClass forName(String className) {
        
        // TODO Auto-generated method stub
        return null;
    }

    public ReflectClass forObject(Object a_object) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isCollection(ReflectClass claxx) {
        // TODO Auto-generated method stub
        return false;
    }

    public void setParent(Reflector reflector) {
        // TODO Auto-generated method stub
        
    }

    public Object deepClone(Object context) {
        // TODO Auto-generated method stub
        return null;
    }

}

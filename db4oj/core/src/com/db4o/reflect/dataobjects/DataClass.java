/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.dataobjects;

import com.db4o.reflect.*;

public class DataClass implements IClass {

    private final String _name;
    private final IClass _superclass;
    private final DataField[] _fields;

    public DataClass(String name, IClass superclass, DataField[] fields) {
        _name = name;
        _superclass = superclass;
        _fields = fields;
        initFieldIndexes();
    }

    private void initFieldIndexes() {
        for (int i = 0; i < _fields.length; i++) {
            _fields[i].setIndex(i);
        }
    }

    public IClass getComponentType() {   //FIXME Find out how this must work.
        return null;
    }

    public IConstructor[] getDeclaredConstructors() {
        return null;
    }

    public IField[] getDeclaredFields() {
        return _fields;
    }

    public IField getDeclaredField(String name) {
        for (int i = 0; i < _fields.length; i++) {
            if (_fields[i].getName().equals(name)) {
                return _fields[i];
            }
        }
        return null;
    }

    public IMethod getMethod(String methodName, IClass[] paramClasses) {
        return null;
    }

    public String getName() {
        return _name;
    }

    public IClass getSuperclass() {
        return _superclass;
    }

    public boolean isAbstract() {  //TODO Consider: Will this method still be necessary once constructor logic is pushed into the reflectors? 
        return false;
    }

    public boolean isArray() {  //FIXME Find out how this must work.
        return false;
    }

    public boolean isAssignableFrom(IClass subclassCandidate) {
        if (subclassCandidate == this) return true;
        if (!(subclassCandidate instanceof DataClass)) return false;
        return isAssignableFrom(subclassCandidate.getSuperclass());
    }

    public boolean isInstance(Object candidate) {
        if (!(candidate instanceof DataObject)) return false;
        return isAssignableFrom(((DataObject)candidate).getDataClass());
    }

    public boolean isInterface() {
        return false;
    }

    public boolean isPrimitive() {
        return false;
    }

    public Object newInstance() {
        return new DataObject(this);
    }


    //FIXME: REFLECTOR Big hack to get a runnable version.
    public Class getJavaClass() {
        return null;
    }

}

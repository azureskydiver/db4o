/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.generic;

import com.db4o.*;
import com.db4o.reflect.*;

public class GenericClass implements ReflectClass {

    private static final GenericField[] NO_FIELDS = new GenericField[0];
    
	private final String _name;
    private final ReflectClass _superclass;
    private GenericField[] _fields = NO_FIELDS;

    public GenericClass(String name, ReflectClass superclass) {
        _name = name;
        _superclass = superclass;
    }
    
	public void initFields(GenericField[] fields) {
		_fields = fields;
		for (int i = 0; i < _fields.length; i++) {
		    _fields[i].setIndex(i);
		}
	}

    public ReflectClass getComponentType() {   //FIXME Find out how this must work.
        return null;
    }

    public ReflectConstructor[] getDeclaredConstructors() {
        return null;
    }

    public ReflectField[] getDeclaredFields() {
        return _fields;
    }

    public ReflectField getDeclaredField(String name) {
        for (int i = 0; i < _fields.length; i++) {
            if (_fields[i].getName().equals(name)) {
                return _fields[i];
            }
        }
        return null;
    }

    public ReflectMethod getMethod(String methodName, ReflectClass[] paramClasses) {
        return null;
    }

    public String getName() {
        return _name;
    }

    public ReflectClass getSuperclass() {
        return _superclass;
    }

    public boolean isAbstract() {  //TODO Consider: Will this method still be necessary once constructor logic is pushed into the reflectors? 
        return false;
    }

    public boolean isArray() {  //FIXME Find out how this must work.
        return false;
    }

    public boolean isAssignableFrom(ReflectClass subclassCandidate) {
        if (subclassCandidate == this) {
        	return true;
        }
        if (!(subclassCandidate instanceof GenericClass)) {
        	return false;
        }
        return isAssignableFrom(subclassCandidate.getSuperclass());
    }

    public boolean isInstance(Object candidate) {
        if (!(candidate instanceof GenericObject)) {
        	return false;
        }
        return isAssignableFrom(((GenericObject)candidate).dataClass());
    }

    public boolean isInterface() {
        return false;
    }

    public boolean isPrimitive() {
        return false;
    }
    
    public boolean isValueType(){
        return false;
    }

    public Object newInstance() {
        return new GenericObject(this);
    }

    public boolean skipConstructor(boolean flag){
        return false;
    }

    public void useConstructor(ReflectConstructor constructor, Object[] params){
        // ignore, we always create a generic object
    }

}

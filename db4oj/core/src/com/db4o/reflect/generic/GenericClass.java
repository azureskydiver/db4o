/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.generic;

import com.db4o.reflect.*;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectConstructor;
import com.db4o.reflect.ReflectField;
import com.db4o.reflect.ReflectMethod;

/**
 * @exclude
 */
public class GenericClass implements ReflectClass {

    private static final GenericField[] NO_FIELDS = new GenericField[0];
    
    private final Reflector _reflector;
    private final ReflectClass _delegate;
    
	private final String _name;
    private final ReflectClass _superclass;
    private GenericField[] _fields = NO_FIELDS;

    public GenericClass(Reflector reflector, ReflectClass delegateClass, String name, ReflectClass superclass) {
        _reflector = reflector;
        _delegate = delegateClass;
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
        if(_delegate != null){
            return _delegate.getComponentType();
        }
        return null;
    }

    public ReflectConstructor[] getDeclaredConstructors() {
        if(_delegate != null){
            return _delegate.getDeclaredConstructors();
        }
        return null;
    }

    public ReflectField[] getDeclaredFields() {
        if(_delegate != null){
            return _delegate.getDeclaredFields();
        }
        return _fields;
    }

    public ReflectField getDeclaredField(String name) {
        if(_delegate != null){
            return _delegate.getDeclaredField(name);
        }
        for (int i = 0; i < _fields.length; i++) {
            if (_fields[i].getName().equals(name)) {
                return _fields[i];
            }
        }
        return null;
    }

    public ReflectMethod getMethod(String methodName, ReflectClass[] paramClasses) {
        if(_delegate != null){
            return _delegate.getMethod(methodName, paramClasses);
        }
        return null;
    }

    public String getName() {
        return _name;
    }

    public ReflectClass getSuperclass() {
        if(_delegate != null){
            return _delegate.getSuperclass();
        }
        return _superclass;
    }

    public boolean isAbstract() {  //TODO Consider: Will this method still be necessary once constructor logic is pushed into the reflectors?
        if(_delegate != null){
            return _delegate.isAbstract();
        }
        return false;
    }

    public boolean isArray() {  //FIXME Find out how this must work.
        if(_delegate != null){
            return _delegate.isArray();
        }
        return false;
    }

    public boolean isAssignableFrom(ReflectClass subclassCandidate) {
        if(_delegate != null){
            return _delegate.isAssignableFrom(subclassCandidate);
        }
        if (subclassCandidate == this) {
        	return true;
        }
        if (!(subclassCandidate instanceof GenericClass)) {
        	return false;
        }
        return isAssignableFrom(subclassCandidate.getSuperclass());
    }

    public boolean isInstance(Object candidate) {
        if(_delegate != null){
            return _delegate.isInstance(candidate);
        }
        if (!(candidate instanceof GenericObject)) {
        	return false;
        }
        return isAssignableFrom(((GenericObject)candidate).genericClass());
    }

    public boolean isInterface() {
        if(_delegate != null){
            return _delegate.isInterface();
        }
        return false;
    }

    public boolean isPrimitive() {
        if(_delegate != null){
            return _delegate.isPrimitive();
        }
        return false;
    }
    
    public Object newInstance() {
        if(_delegate != null){
            return _delegate.newInstance();
        }
        return new GenericObject(this);
    }

    public Reflector reflector() {
        if(_delegate != null){
            return _delegate.reflector();
        }
        return _reflector;
    }
    
    public boolean skipConstructor(boolean flag){
        if(_delegate != null){
            return _delegate.skipConstructor(flag);
        }
        return false;
    }

    public void useConstructor(ReflectConstructor constructor, Object[] params){
        if(_delegate != null){
            _delegate.useConstructor(constructor, params);
        }

        // ignore, we always create a generic object
    }

}

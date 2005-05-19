/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.generic;

import com.db4o.*;
import com.db4o.reflect.*;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectConstructor;
import com.db4o.reflect.ReflectField;
import com.db4o.reflect.ReflectMethod;

/**
 * @exclude
 */
public class GenericClass implements ReflectClass, DeepClone {

    private static final GenericField[] NO_FIELDS = new GenericField[0];
    
    private final GenericReflector _reflector;
    private final ReflectClass _delegate;
    
    private final String _name;
    private ReflectClass _superclass;
    
    private GenericClass _array;
    
    private boolean _isSecondClass;
    
    private GenericField[] _fields = NO_FIELDS;

    public GenericClass(GenericReflector reflector, ReflectClass delegateClass, String name, ReflectClass superclass) {
        _reflector = reflector;
        _delegate = delegateClass;
        _name = name;
        _superclass = superclass;
    }
    
    public GenericClass arrayClass(){
        if(_array != null){
            return _array;
        }
        _array = new GenericArrayClass(_reflector, this, _name, _superclass);
        _array._isSecondClass = _isSecondClass;
        return _array;
    }

    public Object deepClone(Object obj) {
        GenericReflector reflector = (GenericReflector)obj;
        ReflectClass superClass = null;
        if(_superclass != null){
            _superclass = reflector.forName(_superclass.getName());
        }
        GenericClass ret = new GenericClass(reflector, _delegate, _name, superClass);
        ret._isSecondClass = _isSecondClass;
        GenericField[] fields = new GenericField[_fields.length];
        for (int i = 0; i < fields.length; i++) {
            fields[i] = (GenericField)_fields[i].deepClone(reflector);
        }
        ret.initFields(fields);
        return ret;
    }
    
    public ReflectClass getComponentType() {
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
    
    
    // TODO: consider that classes may have two fields of
    // the same name after refactoring.

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

    public ReflectField[] getDeclaredFields() {
        if(_delegate != null){
            return _delegate.getDeclaredFields();
        }
        return _fields;
    }
    
    public ReflectClass getDelegate(){
    	if(_delegate != null){
    		return _delegate;
    	}
        return this;
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
        if(_superclass != null){
            return _superclass;
        }
        if(_delegate == null){
            return null;
        }
        ReflectClass delegateSuperclass = _delegate.getSuperclass();
        if(delegateSuperclass != null){
            _superclass = _reflector.ensureDelegate(delegateSuperclass);
        }
        return _superclass;
    }
    
	public void initFields(GenericField[] fields) {
		_fields = fields;
		for (int i = 0; i < _fields.length; i++) {
		    _fields[i].setIndex(i);
		}
	}

	 // TODO: Consider: Will this method still be necessary 
	// once constructor logic is pushed into the reflectors?
    public boolean isAbstract() { 
        if(_delegate != null){
            return _delegate.isAbstract();
        }
        return false;
    }

    public boolean isArray() {
        if(_delegate != null){
            return _delegate.isArray();
        }
        return false;
    }

    public boolean isAssignableFrom(ReflectClass subclassCandidate) {
    	
    	if(subclassCandidate == null){
    		return false;
    	}
    	
        if(_delegate != null){
        	if( subclassCandidate instanceof GenericClass){
        		subclassCandidate = ((GenericClass)subclassCandidate).getDelegate();
        	}
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

	public boolean isCollection() {
		return _reflector.isCollection(this);
	}
	
    public boolean isInstance(Object candidate) {
        if(_delegate != null){
            return _delegate.isInstance(candidate);
        }
        if (!(candidate instanceof GenericObject)) {
        	return false;
        }
        return isAssignableFrom(((GenericObject)candidate)._class);
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
    
    public boolean isSecondClass() {
        if(isPrimitive()){
            return true;
        }
        return _isSecondClass;
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
    
    void setSecondClass(){
        _isSecondClass = true;
    }
    
    public boolean skipConstructor(boolean flag){
        if(_delegate != null){
            return _delegate.skipConstructor(flag);
        }
        return false;
    }
	
	public Object[] toArray(Object obj){
		if(! isCollection()){
			return new Object[]{obj};
		}
		return Platform.collectionToArray(_reflector.getStream(), obj);
	}
    
    public String toString(){
        return "GenericClass " + _name; 
    }

    public void useConstructor(ReflectConstructor constructor, Object[] params){
        if(_delegate != null){
            _delegate.useConstructor(constructor, params);
        }

        // ignore, we always create a generic object
    }

}

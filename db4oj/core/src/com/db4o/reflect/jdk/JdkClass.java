/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.jdk;

import java.lang.reflect.*;

import com.db4o.*;
import com.db4o.reflect.*;

/**
 * Reflection implementation for Class to map to JDK reflection.
 */
public class JdkClass implements ReflectClass{
	
	private final Reflector _reflector;
	private final Class _clazz;
    private ReflectConstructor _constructor;
    private Object[] _constructorParams;
	
	public JdkClass(Reflector reflector, Class clazz) {
        if(reflector == null){
            throw new NullPointerException();
        }
		_reflector = reflector;
		_clazz = clazz;
	}
    
	public ReflectClass getComponentType() {
		return _reflector.forClass(_clazz.getComponentType());
	}

	public ReflectConstructor[] getDeclaredConstructors(){
		Constructor[] constructors = _clazz.getDeclaredConstructors();
		ReflectConstructor[] reflectors = new ReflectConstructor[constructors.length];
		for (int i = 0; i < constructors.length; i++) {
			reflectors[i] = new JdkConstructor(_reflector, constructors[i]);
		}
		return reflectors;
	}
	
	public ReflectField getDeclaredField(String name){
		try {
			return new JdkField(_reflector, _clazz.getDeclaredField(name));
		} catch (Exception e) {
			return null;
		}
	}
	
	public ReflectField[] getDeclaredFields(){
		Field[] fields = _clazz.getDeclaredFields();
		ReflectField[] reflectors = new ReflectField[fields.length];
		for (int i = 0; i < reflectors.length; i++) {
			reflectors[i] = new JdkField(_reflector, fields[i]);
		}
		return reflectors;
	}
    
    public ReflectClass getDelegate(){
        return this;
    }
	
	public ReflectMethod getMethod(String methodName, ReflectClass[] paramClasses){
		try {
			Method method = _clazz.getMethod(methodName, JdkReflector.toNative(paramClasses));
			if(method == null){
				return null;
			}
			return new JdkMethod(method, reflector());
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getName(){
		return _clazz.getName();
	}
	
	public ReflectClass getSuperclass() {
		return _reflector.forClass(_clazz.getSuperclass());
	}
	
	public boolean isAbstract(){
		return Modifier.isAbstract(_clazz.getModifiers());
	}
	
	public boolean isArray() {
		return _clazz.isArray();
	}

	public boolean isAssignableFrom(ReflectClass type) {
		if(!(type instanceof JdkClass)) {
			return false;
		}
		return _clazz.isAssignableFrom(((JdkClass)type).getJavaClass());
	}
	
	public boolean isCollection() {
		return _reflector.isCollection(this);
	}
	
	public boolean isInstance(Object obj) {
		return _clazz.isInstance(obj);
	}
	
	public boolean isInterface(){
		return _clazz.isInterface();
	}
	
	public boolean isPrimitive() {
		return _clazz.isPrimitive();
	}
    
    public boolean isSecondClass() {
        return isPrimitive();
    }
    
    public Object newInstance(){
		try {
            if(_constructor == null){
                return _clazz.newInstance();
            }
            return _constructor.newInstance(_constructorParams);
		} catch (Throwable t) {
		} 
		return null;
	}
	
	Class getJavaClass(){
		return _clazz;
	}
    
    public Reflector reflector() {
        return _reflector;
    }
	
    public boolean skipConstructor(boolean flag){
        if(flag){
            Constructor constructor = Platform4.jdk().serializableConstructor(_clazz);
            if(constructor != null){
                try{
                    Object o = constructor.newInstance(null);
                    if(o != null){
                        useConstructor(new JdkConstructor(_reflector, constructor), null);
                        return true;
                    }
                }catch(Exception e){
                    
                }
            }
        }
        useConstructor(null, null);
        return false;
    }
	
    public void useConstructor(ReflectConstructor constructor, Object[] params){
        this._constructor = constructor;
        _constructorParams = params;
    }

}

/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.jdk;

import java.lang.reflect.*;

import com.db4o.*;
import com.db4o.reflect.*;

/**
 * Reflection implementation for Class to map to JDK reflection.
 */
public class JdkClass implements ReflectClass{
	
	private final Reflector reflector;
	private final Class clazz;
    private ReflectConstructor constructor;
    private Object[] constructorParams;
	
	public JdkClass(Reflector reflector, Class clazz) {
		this.reflector = reflector;
		this.clazz = clazz;
	}
    
	public ReflectClass getComponentType() {
		return reflector.forClass(clazz.getComponentType());
	}

	public ReflectConstructor[] getDeclaredConstructors(){
		Constructor[] constructors = clazz.getDeclaredConstructors();
		ReflectConstructor[] reflectors = new ReflectConstructor[constructors.length];
		for (int i = 0; i < constructors.length; i++) {
			reflectors[i] = new JdkConstructor(reflector, constructors[i]);
		}
		return reflectors;
	}
	
	public ReflectField getDeclaredField(String name){
		try {
			return new JdkField(reflector, clazz.getDeclaredField(name));
		} catch (Exception e) {
			return null;
		}
	}
	
	public ReflectField[] getDeclaredFields(){
		Field[] fields = clazz.getDeclaredFields();
		ReflectField[] reflectors = new ReflectField[fields.length];
		for (int i = 0; i < reflectors.length; i++) {
			reflectors[i] = new JdkField(reflector, fields[i]);
		}
		return reflectors;
	}
	
	public ReflectMethod getMethod(String methodName, ReflectClass[] paramClasses){
		try {
			Method method = clazz.getMethod(methodName, JdkReflector.toNative(paramClasses));
			if(method == null){
				return null;
			}
			return new JdkMethod(method);
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getName(){
		return clazz.getName();
	}
	
	public ReflectClass getSuperclass() {
		return reflector.forClass(clazz.getSuperclass());
	}
	
	public boolean isAbstract(){
		return Modifier.isAbstract(clazz.getModifiers());
	}
	
	public boolean isArray() {
		return clazz.isArray();
	}

	public boolean isAssignableFrom(ReflectClass type) {
		if(!(type instanceof JdkClass)) {
			return false;
		}
		return clazz.isAssignableFrom(((JdkClass)type).getJavaClass());
	}
	
	public boolean isInstance(Object obj) {
		return clazz.isInstance(obj);
	}
	
	public boolean isInterface(){
		return clazz.isInterface();
	}
	
	public boolean isPrimitive() {
		return clazz.isPrimitive();
	}
    
    public boolean isValueType(){
        return Platform.isValueType(clazz);
    }
	
	public Object newInstance(){
		try {
            if(constructor == null){
                return clazz.newInstance();
            }
            return constructor.newInstance(constructorParams);
		} catch (Throwable t) {
		} 
		return null;
	}
	
	Class getJavaClass(){
		return clazz;
	}
	
    public boolean skipConstructor(boolean flag){
        if(flag){
            Constructor constructor = Platform.jdk().serializableConstructor(clazz);
            if(constructor != null){
                try{
                    Object o = constructor.newInstance(null);
                    if(o != null){
                        useConstructor(new JdkConstructor(reflector, constructor), null);
                        return true;
                    }
                }catch(Exception e){
                    
                }
            }
        }
        useConstructor(null, null);
        return false;
    }
    
	public String toString(){
		return "CClass: " + clazz.getName();
	}
    
    public void useConstructor(ReflectConstructor constructor, Object[] params){
        this.constructor = constructor;
        constructorParams = params;
    }

}

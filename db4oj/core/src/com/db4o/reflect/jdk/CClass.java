/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.jdk;

import java.lang.reflect.*;

import com.db4o.reflect.*;

/**
 * Reflection implementation for Class to map to JDK reflection.
 */
public class CClass implements IClass{
	
	private final IReflect reflector;
	private final Class clazz;
	
	public CClass(IReflect reflector, Class clazz) {
		this.reflector = reflector;
		this.clazz = clazz;
	}
	
	public IConstructor[] getDeclaredConstructors(){
		Constructor[] constructors = clazz.getDeclaredConstructors();
		IConstructor[] reflectors = new IConstructor[constructors.length];
		for (int i = 0; i < constructors.length; i++) {
			reflectors[i] = new CConstructor(reflector, constructors[i]);
		}
		return reflectors;
	}
	
	public IField getDeclaredField(String name){
		try {
			return new CField(clazz.getDeclaredField(name));
		} catch (Exception e) {
			return null;
		}
	}
	
	public IField[] getDeclaredFields(){
		Field[] fields = clazz.getDeclaredFields();
		IField[] reflectors = new IField[fields.length];
		for (int i = 0; i < reflectors.length; i++) {
			reflectors[i] = new CField(fields[i]);
		}
		return reflectors;
	}
	
	public IMethod getMethod(String methodName, IClass[] paramClasses){
		try {
			Method method = clazz.getMethod(methodName, CReflect.toNative(paramClasses));
			if(method == null){
				return null;
			}
			return new CMethod(method);
		} catch (Exception e) {
			return null;
		}
	}
	
	public boolean isAbstract(){
		return Modifier.isAbstract(clazz.getModifiers());
	}
	
	public boolean isInterface(){
		return clazz.isInterface();
	}
	
	public Object newInstance(){
		try {
			return clazz.newInstance();
		} catch (Throwable t) {
		} 
		return null;
	}
	
	Class getJavaClass(){
		return clazz;
	}
	
}

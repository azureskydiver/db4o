/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.reflect.self;

import com.db4o.reflect.*;

public class SelfClass implements ReflectClass {

	private final SelfReflector _reflector;

	private final Class _class;

	public SelfClass(SelfReflector reflector, Class clazz) {
		_reflector = reflector;
		_class = clazz;

	}

	public Class getJavaClass() {
		return _class;
	}
	

	public Reflector reflector() {
		return _reflector;
	}
	

	public ReflectClass getComponentType() {
		return _reflector.forClass(_class.getComponentType());
	}

	public ReflectConstructor[] getDeclaredConstructors() {
		return new SelfConstructor[]{new SelfConstructor(_class)};
	}

	public ReflectField[] getDeclaredFields() {
		return _reflector._registry.fieldsFor(_class);
	}

	public ReflectField getDeclaredField(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public ReflectClass getDelegate() {
		// TODO Auto-generated method stub
		return null;
	}

	public ReflectMethod getMethod(String methodName,
			ReflectClass[] paramClasses) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return _class.getName();
	}

	public ReflectClass getSuperclass() {
		return _reflector.forClass(_class.getSuperclass());
	}

	public boolean isAbstract() {
		// TODO
		return false;
	}

	public boolean isArray() {
		return false;
	}

	public boolean isAssignableFrom(ReflectClass type) {
		if (!(type instanceof SelfClass)) {
			return false;
		}
		return _class.isAssignableFrom(((SelfClass)type).getJavaClass());
	}

	public boolean isCollection() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isInstance(Object obj) {
		return _class.isInstance(obj);
	}

	public boolean isInterface() {
		return _class.isInterface();
	}

	public boolean isPrimitive() {
		return _class.isPrimitive();
	}

	public boolean isSecondClass() {
		// TODO Auto-generated method stub
		return false;
	}

	public Object newInstance() {
		try {
			return _class.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	

	public boolean skipConstructor(boolean flag) {
		// cannot skip constructors, only available for JDK1.4+
		return false;
	}

	public void useConstructor(ReflectConstructor constructor, Object[] params) {
		// ignore, there must be a public no-args constructor suitable for Class.newInstance()
	}

	public Object[] toArray(Object obj) {
		return null;
	}

}

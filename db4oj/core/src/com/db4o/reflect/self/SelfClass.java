/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.reflect.self;

import com.db4o.reflect.*;

public class SelfClass implements ReflectClass {
	private SelfField[] fields;

	private SelfReflector _reflector;

	private Class _class;
	

//	public SelfClass() {
//		super();
//	}

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
		return new SelfConstructor[] { new SelfConstructor(_class) };
	}
	
	public ReflectField[] getDeclaredFields() {
		ensureFieldsLoaded();
		return fields;
	}

	private void ensureFieldsLoaded() {
		if(fields==null) {
			FieldInfo[] fieldInfo=_reflector._registry.fieldsFor(_class);
			if(fieldInfo==null) {
				fields=new SelfField[0];
				return;
			}
			fields=new SelfField[fieldInfo.length];
			for(int idx=0;idx<fieldInfo.length;idx++) {
				fields[idx]=selfFieldFor(fieldInfo[idx]);
			}
		}
	}
	
	public ReflectField getDeclaredField(String name) {
		ensureFieldsLoaded();
		for(int idx=0;idx<fields.length;idx++) {
			if(fields[idx].getName().equals(name)) {
				return fields[idx];
			}
		}
		return null;
	}

	private SelfField selfFieldFor(FieldInfo fieldInfo) {
		return new SelfField(fieldInfo.name(),(SelfClass)_reflector.forClass(fieldInfo.type()));
	}

	public ReflectClass getDelegate() {
		return this;
	}

	public ReflectMethod getMethod(String methodName,
			ReflectClass[] paramClasses) {
		// TODO !!!!
		return null;
	}

	public String getName() {
		return _class.getName();
	}

	public ReflectClass getSuperclass() {		
		Class clazz = _class.getSuperclass();
		if(clazz==null) {
			return null;
		}
		return _reflector.forClass(clazz);
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
		return _class.isAssignableFrom(((SelfClass) type).getJavaClass());
	}

	public boolean isCollection() {
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
		return isPrimitive();
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
		// ignore, there must be a public no-args constructor suitable for
		// Class.newInstance()
	}

	public Object[] toArray(Object obj) {
		return null;
	}

}

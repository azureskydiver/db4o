/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.reflect.self;

import com.db4o.reflect.*;

public class SelfReflector implements Reflector {

	SelfReflectionRegistry _registry;
	private Reflector _parent;
	
	public SelfReflector(SelfReflectionRegistry registry) {
		_registry= registry;
	}
	
	public ReflectArray array() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean constructorCallsSupported() {
		return true;
	}

	public ReflectClass forClass(Class clazz) {
		return new SelfClass(this, clazz);
	}

	public ReflectClass forName(String className) {
		try {
			Class clazz=Class.forName(className);
			return forClass(clazz);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public ReflectClass forObject(Object a_object) {
		if (a_object == null) {
			return null;
		}
		return forClass(a_object.getClass());
	}

	public boolean isCollection(ReflectClass claxx) {
		return false;
	}

	public void setParent(Reflector reflector) {
		_parent = reflector;
	}

	public Object deepClone(Object context) {
		// TODO !!!
		return new SelfReflector(_registry);
	}


	public boolean isKnownClass(Class clazz) {
		return _registry.fieldsFor(clazz)!=null;
	}
}

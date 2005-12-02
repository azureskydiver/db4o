/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.reflect.self;

import com.db4o.reflect.*;

public class SelfReflector implements Reflector {

	private Reflector _parent;

	public ReflectArray array() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean constructorCallsSupported() {
		return true;
	}

	public ReflectClass forClass(Class clazz) {
		return new SelfClass(_parent, clazz);
	}

	public ReflectClass forName(String className) {

		// TODO Auto-generated method stub
		return null;
	}

	public ReflectClass forObject(Object a_object) {
		if (a_object == null) {
			return null;
		}
		return _parent.forClass(a_object.getClass());
	}

	public boolean isCollection(ReflectClass claxx) {
		return false;
	}

	public void setParent(Reflector reflector) {
		_parent = reflector;
	}

	public Object deepClone(Object context) {
		// TODO Auto-generated method stub
		return null;
	}

}

/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.reflect.self;

import com.db4o.internal.ReflectPlatform;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectConstructor;

public class SelfConstructor implements ReflectConstructor{

	private Class _class;
	
	public SelfConstructor(Class clazz) {
		_class = clazz;
	}

	public void setAccessible() {
	}
	
	public ReflectClass[] getParameterTypes() {
		return new ReflectClass[] {};
	}

	public Object newInstance(Object[] parameters) {
		return ReflectPlatform.createInstance(_class);
	}

}

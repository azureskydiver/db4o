package com.db4o.reflect.core;

import com.db4o.reflect.*;

public class ReflectConstructorSpec {
	private ReflectConstructor _constructor;
	private Object[] _args;

	public ReflectConstructorSpec(ReflectConstructor constructor, Object[] args) {
		_constructor = constructor;
		_args = args;
	}
	
	public Object newInstance() {
		return _constructor.newInstance(_args);
	}
}

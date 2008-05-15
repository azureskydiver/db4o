package com.db4o.reflect.core;

import com.db4o.foundation.*;

public class ReflectConstructorSpec {
	private ReflectConstructor _constructor;
	private Object[] _args;
	private TernaryBool _canBeInstantiated;

	public static final ReflectConstructorSpec UNSPECIFIED_CONSTRUCTOR =
		new ReflectConstructorSpec(TernaryBool.UNSPECIFIED);

	public static final ReflectConstructorSpec INVALID_CONSTRUCTOR =
		new ReflectConstructorSpec(TernaryBool.NO);

	public ReflectConstructorSpec(ReflectConstructor constructor, Object[] args) {
		_constructor = constructor;
		_args = args;
		_canBeInstantiated = TernaryBool.YES; 
	}
	
	private ReflectConstructorSpec(TernaryBool canBeInstantiated) {
		_canBeInstantiated = canBeInstantiated;
		_constructor = null;
	}
	
	public Object newInstance() {
		if(_constructor == null) {
			return null;
		}
		return _constructor.newInstance(_args);
	}
	
	public TernaryBool canBeInstantiated(){
		return _canBeInstantiated;
	}
}

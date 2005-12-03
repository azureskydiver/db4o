package com.db4o.reflect.self;

public class FieldInfo {
	private String _name;
	private Class _clazz;

	public FieldInfo(String name, Class clazz) {
		this._name = name;
		this._clazz = clazz;
	}
	
	public String name() {
		return _name;
	}
	
	public Class type() {
		return _clazz;
	}
}

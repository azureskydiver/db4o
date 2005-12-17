package com.db4o.reflect.self;

public abstract class Animal implements SelfReflectable, Being {
	private String _name;


	protected Animal(String name) {
		_name = name;
	}

	public String name() {
		return _name;
	}

	public Object self_get(String fieldName) {
		if(fieldName.equals("_name")) {
			return _name;
		}
		return null;
	}

	public void self_set(String fieldName,Object value) {
		if(fieldName.equals("_name")) {
			_name=(String)value;
		}
	}
}

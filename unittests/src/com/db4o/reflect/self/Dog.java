package com.db4o.reflect.self;

public class Dog implements SelfReflectable {
	private String _name;

	public Dog() {
	}
	
	public Dog(String _name) {
		this._name = _name;
	}
	
	public String name() {
		return _name;
	}
	
	public String toString() {
		return "DOG: "+_name;
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

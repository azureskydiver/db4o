package com.db4o.reflect.self;

import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;

public class SelfField implements ReflectField {

	private String _name;
	private ReflectClass _type;

	public SelfField(String name, ReflectClass type) {
		_name = name;
		_type = type;
	}

	public Object get(Object onObject) {
		if(onObject instanceof SelfReflectable) {
			return ((SelfReflectable)onObject).self_get(_name);
		}
		return null;
	}

	public String getName() {
		return _name;
	}

	public ReflectClass getType() {
		return _type;
	}

	public boolean isPublic() {
		return true;
	}

	public boolean isStatic() {
		return false;
	}

	public boolean isTransient() {
		return false;
	}

	public void set(Object onObject, Object value) {
		if(onObject instanceof SelfReflectable) {
			((SelfReflectable)onObject).self_set(_name, value);
		}
	}

	public void setAccessible() {
		// TODO Auto-generated method stub

	}

}

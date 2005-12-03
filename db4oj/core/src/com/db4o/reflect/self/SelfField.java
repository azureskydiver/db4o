package com.db4o.reflect.self;

import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;

public class SelfField implements ReflectField {

	private String _name;
	private SelfClass _type;

	public SelfField(String name, SelfClass type) {
		_name = name;
		_type = type;
	}

	public Object get(Object onObject) {
		if(onObject instanceof SelfReflectable) {
			return ((SelfReflectable)onObject).db4o$get(_name);
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
		return false;
	}

	public boolean isStatic() {
		return false;
	}

	public boolean isTransient() {
		return false;
	}

	public void set(Object onObject, Object value) {
		// TODO Auto-generated method stub

	}

	public void setAccessible() {
		// TODO Auto-generated method stub

	}

}

package com.db4o.reflect.self;

import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;

public class SelfField implements ReflectField {

	private String _name;

	private Class _type;

	public SelfField(String name, Class type) {
		_name = name;
		_type = type;
	}

	public Object get(Object onObject) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return _name;
	}

	public ReflectClass getType() {
		return new SelfClass(new SelfReflector(), _type).getDeclaredField(_name).getType();
	}

	public boolean isPublic() {
		return new SelfClass(new SelfReflector(), _type).getDeclaredField(_name).isPublic();
	}

	public boolean isStatic() {
		return new SelfClass(new SelfReflector(), _type).getDeclaredField(_name).isStatic();
	}

	public boolean isTransient() {
		return new SelfClass(new SelfReflector(), _type).getDeclaredField(_name).isTransient();
	}

	public void set(Object onObject, Object value) {
		// TODO Auto-generated method stub

	}

	public void setAccessible() {
		// TODO Auto-generated method stub

	}

}

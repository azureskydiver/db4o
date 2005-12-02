package com.db4o.reflect.self;

import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;

public class SelfField implements ReflectField{
	private String _name;
	private Class _type;
	
	
	public SelfField(String name, Class type) {
		_name=name;
		_type=type;
	}

	public Object get(Object onObject) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {

		return null;
	}

	public ReflectClass getType() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isPublic() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isStatic() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isTransient() {
		// TODO Auto-generated method stub
		return false;
	}

	public void set(Object onObject, Object value) {
		// TODO Auto-generated method stub
		
	}

	public void setAccessible() {
		// TODO Auto-generated method stub
		
	}

}

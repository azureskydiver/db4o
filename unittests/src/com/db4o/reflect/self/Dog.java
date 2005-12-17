package com.db4o.reflect.self;

public class Dog extends Animal {
	private int _age;
	
	public Dog() {
		this(null,0);
	}
	
	public Dog(String name,int age) {
		super(name);
		_age=age;
	}

	public int age() {
		return _age;
	}
	
	public String toString() {
		return "DOG: "+name()+"/"+age();
	}
	
	public Object self_get(String fieldName) {
		if(fieldName.equals("_age")) {
			return new Integer(_age);
		}
		return super.self_get(fieldName);
	}

	public void self_set(String fieldName,Object value) {
		if(fieldName.equals("_age")) {
			_age=((Integer)value).intValue();
			return;
		}
		super.self_set(fieldName,value);
	}
}

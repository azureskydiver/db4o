package com.db4o.reflect.self;

public class Dog extends Animal {
	private Dog[] _parents;
	private int _age;
	
	public Dog() {
		this(null,0,new Dog[0]);
	}
	
	public Dog(String name,int age,Dog[] parents) {
		super(name);
		_age=age;
		_parents=parents;
	}

	public int age() {
		return _age;
	}

	public Dog[] parents() {
		return _parents;
	}
	
	public String toString() {
		return "DOG: "+name()+"/"+age()+"/"+_parents.length;
	}
	
	public Object self_get(String fieldName) {
		if(fieldName.equals("_age")) {
			return new Integer(_age);
		}
		if(fieldName.equals("_parents")) {
			return _parents;
		}
		return super.self_get(fieldName);
	}

	public void self_set(String fieldName,Object value) {
		if(fieldName.equals("_age")) {
			_age=((Integer)value).intValue();
			return;
		}
		if(fieldName.equals("_parents")) {
			_parents=(Dog[])value;
			return;
		}
		super.self_set(fieldName,value);
	}
}

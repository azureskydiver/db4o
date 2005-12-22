package com.db4o.reflect.self;

public class Dog extends Animal {
	public Dog[] _parents;
	public int _age;
	private int[] _prices;
	
	public Dog() {
		this(null,0,new Dog[0],new int[0]);
	}
	
	public Dog(String name,int age,Dog[] parents,int[] prices) {
		super(name);
		_age=age;
		_parents=parents;
		_prices=prices;
	}

	public int age() {
		return _age;
	}

	public Dog[] parents() {
		return _parents;
	}
	
	public int[] prices() {
		return _prices;
	}
	
	public String toString() {
		return "DOG: "+name()+"/"+age()+"/"+_parents.length+"/"+_prices.length;
	}
	
	public Object self_get(String fieldName) {
		if(fieldName.equals("_age")) {
			return new Integer(_age);
		}
		if(fieldName.equals("_parents")) {
			return _parents;
		}
		if(fieldName.equals("_prices")) {
			return _prices;
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
		if(fieldName.equals("_prices")) {
			_prices=(int[])value;
			return;
		}
		super.self_set(fieldName,value);
	}
}

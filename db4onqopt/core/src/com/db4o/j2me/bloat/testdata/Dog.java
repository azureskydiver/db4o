package com.db4o.j2me.bloat.testdata;

public class Dog extends Animal {
	public Dog[] _parents;
	public int _age;
//	private int[] _prices;
	
	public Dog(String name,int age,Dog[] parents,int[] prices) {
		super(name);
		_age=age;
		_parents=parents;
//		_prices=prices;
	}

	public int age() {
		return 0;//_age;
	}

	public Dog[] parents() {
		return _parents;
	}
	
//	public int[] prices() {
//		return _prices;
//	}
	
	public String toString() {
		return "DOG: "+name()+"/"+age();//+"/"+_parents.length+"/"+_prices.length;
	}
}

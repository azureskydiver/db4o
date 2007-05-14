package com.db4o.test.nativequery.analysis;

class Data extends Base {
	boolean bool;
	float value;
	float otherValue;
	String name;
	Data next;
	int[] intArray;
	Data[] objArray;
	Boolean boolWrapper;
	
	public boolean getBool() {
		return bool;
	}
	
	public float getValue() {
		return value;
	}
	public float getValue(int times) {
		return otherValue;
	}
	public String getName() {
		return name;
	}
	public Data getNext() {
		return next;
	}
	
	public boolean hasNext() {
		return getNext()!=null;
	}
	
	public void someMethod() {
		System.out.println();
	}
}

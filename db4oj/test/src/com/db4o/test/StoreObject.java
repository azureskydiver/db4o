package com.db4o.test;

public class StoreObject {

	Object _field;
	
	public void storeOne()	{
		_field = new Object();
	}
	
	public void testOne() {
		Test.ensure(_field != null);
	}
}

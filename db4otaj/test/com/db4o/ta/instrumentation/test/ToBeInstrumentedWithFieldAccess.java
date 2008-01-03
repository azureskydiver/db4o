package com.db4o.ta.instrumentation.test;

public class ToBeInstrumentedWithFieldAccess {

	private int _id;

	public boolean compareID(ToBeInstrumentedWithFieldAccess other) {
		return _id == other._id;
	}
	
	public void setId(int id) {
		_id = id;
	}
}

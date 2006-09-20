/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

public class Car {

	String _model;
	Pilot _pilot;

	public Car() {
	}

	public Car(String model) {
		_model = model;
	}

	public String getModel() {
		return _model;
	}

	public void setModel(String model) {
		_model = model;
	}

}

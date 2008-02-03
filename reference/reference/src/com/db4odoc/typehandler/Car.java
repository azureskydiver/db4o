/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.typehandler;

public class Car {
	private StringBuffer model;

	public Car(String model) {
		this.model = model == null? null : new StringBuffer(model);
	}


	public String getModel() {
		return model.toString();
	}

	public String toString() {
		return model == null? null : model.toString();
	}

}
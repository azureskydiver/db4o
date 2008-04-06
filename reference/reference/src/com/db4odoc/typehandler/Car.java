/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.typehandler;

public class Car {

	private StringBuffer model;
	private StringBuffer modelCopy;

	public Car(String model) {
		this.model = new StringBuffer(model);
		modelCopy = new StringBuffer("Copy: " + model);
	}


	public String getModel() {
		return model.toString();
	}

	public String toString() {
		return model == null? "null" : model.toString() + " " + modelCopy.toString();
	}

}
/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.translators;

public class Car {
	private String model;

	public Car(String model) {
		this.model = model;
	}

	public String getModel() {
		return model;
	}

	public String toString() {
		return model;
	}
}
package com.db4o.cs.generic;

import java.io.Serializable;

/**
 * User: treeder
 * Date: Oct 30, 2006
 * Time: 10:40:09 PM
 */
public class Person implements Serializable {
	private int index;
	private String name;


	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String toString() {
		return index + " " + name + " - " + super.toString();
	}
}

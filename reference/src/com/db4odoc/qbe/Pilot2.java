/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.qbe;

public class Pilot2 {
	private String name;

	private int points = 100;

	public Pilot2(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public int getPoints() {
		return points;
	}

	public String toString() {
		return name + "/" + points;
	}

}

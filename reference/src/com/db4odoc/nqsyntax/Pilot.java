/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.nqsyntax;

public class Pilot {
	private String name;

	private int points;

	public Pilot(String name, int points) {
		this.name = name;
		this.points = points;
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

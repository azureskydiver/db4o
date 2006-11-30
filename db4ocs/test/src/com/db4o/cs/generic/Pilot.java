/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.generic;

public class Pilot {
	String name;

	int points;

	public Pilot(String s, int i) {
		super();
		this.name = s;
		this.points = i;
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

	public void setPoints(int points) {
		this.points = points;
	}

	public void addPoints(int p) {
		points += p;
	}
	
	public String toString() {
		return name + points;
	}
}

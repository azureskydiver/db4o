/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.nqcollection;


public class Trainee implements Person {
	
	private String name;
	private Pilot instructor;

	public Trainee(String name, Pilot pilot) {
		this.name = name;
		this.instructor = pilot;
	}
	
	public String getName() {
		return name;
	}
	
	public Pilot getInstructor() {
		return instructor;
	}

	public String toString() {
		return name + "(" + instructor + ")"; 
	}
}

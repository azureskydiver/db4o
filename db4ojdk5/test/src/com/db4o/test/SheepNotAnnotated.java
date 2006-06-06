package com.db4o.test;


public class SheepNotAnnotated {
	
	private String name;
	
	SheepNotAnnotated parent;
	public SheepNotAnnotated(String name, SheepNotAnnotated parent) {
		this.name = name;
		this.parent = parent;
	}
@Override
public String toString() {
	return name+ " "+ parent;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
}

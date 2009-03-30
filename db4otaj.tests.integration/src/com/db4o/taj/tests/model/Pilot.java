package com.db4o.taj.tests.model;

public class Pilot extends Person {

	private int _points;
	
	public Pilot(String name, int points) {
		super(name);
		_points = points;
	}
	
	public int points() {
		return _points;
	}
	
	public void points(int points) {
		_points = points;
	}
	
	public String toString() {
		return super.toString() + ": " + _points;
	}
	
}

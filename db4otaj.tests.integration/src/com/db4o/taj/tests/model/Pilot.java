package com.db4o.taj.tests.model;

public class Pilot {

	private String _name;
	private int _points;
	
	public Pilot(String name, int points) {
		_name = name;
		_points = points;
	}
	
	public String name() {
		return _name;
	}
	
	public int points() {
		return _points;
	}
	
	public void points(int points) {
		_points = points;
	}
	
	public String toString() {
		return _name + ": " + _points;
	}
	
}

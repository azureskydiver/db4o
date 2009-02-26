package com.db4o.taj.tests.model;

import java.util.*;

public class Team {

	private String _name;
	private List _pilots;
	
	public Team(String name) {
		_name = name;
		_pilots = new ArrayList();
	}
	
	public void addPilot(Pilot pilot) {
		_pilots.add(pilot);
	}
	
	public List pilots() {
		return _pilots;
	}

	public String toString() {
		return _name + ": " + _pilots + "";
	}
}

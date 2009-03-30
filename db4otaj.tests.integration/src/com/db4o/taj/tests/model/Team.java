package com.db4o.taj.tests.model;

import java.util.*;

public class Team {

	private String _name;
	private List _pilots;
	private List _mechanics;
	private Map _sponsors;
	
	public Team(String name) {
		_name = name;
		_pilots = new ArrayList();
		_mechanics = new LinkedList();
		_sponsors = new HashMap();
	}
	
	public void addPilot(Pilot pilot) {
		_pilots.add(pilot);
	}

	public void addMechanic(Person mechanic) {
		_mechanics.add(mechanic);
	}

	public void addSponsor(String name, int amount) {
		_sponsors.put(name, new Integer(amount));
	}

	public List pilots() {
		return _pilots;
	}

	public List mechanics() {
		return _mechanics;
	}

	public int amountSponsored(String name) {
		return ((Integer)_sponsors.get(name)).intValue();
	}
	
	public String toString() {
		return _name + ": " + _pilots + "";
	}
}

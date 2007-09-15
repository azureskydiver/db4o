package com.db4odoc.inconsistent;

public class Car {

	private String name;

	private int model;

	private Pilot pilot;

	public Car(String name, int model, Pilot pilot) {
		this.name = name;
		this.model = model;
		this.pilot = pilot;
	}

	public void setModel(int model) {
		this.model = model;
	}
	
	public void setPilot(Pilot pilot) {
		this.pilot = pilot;
	}
	
	public String toString() {
		return "Car: " + name + " " + model + " Pilot: " + pilot.getName();
	}
}

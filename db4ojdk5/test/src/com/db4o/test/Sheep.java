package com.db4o.test;

import com.db4o.config.annotations.*;

@Cascade
public class Sheep {

	@Index @Cascade
	private String name;

	Sheep parent;

	public Sheep(String name, Sheep parent) {
		this.name = name;
		this.parent = parent;
	}

	@Override 
	public String toString() {
		return name + " " + parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

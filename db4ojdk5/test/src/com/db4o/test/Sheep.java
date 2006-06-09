package com.db4o.test;

import com.db4o.config.annotations.*;

@Cascade({CascadeType.ACTIVATE})
@CallConstructor
public class Sheep {

	@Index @Cascade({CascadeType.UPDATE})
	private String name;
	private boolean constructorCalled=false;

	Sheep parent;

	public Sheep(String name, Sheep parent) {
		this.name = name;
		this.parent = parent;
		constructorCalled=true;
	}

	public boolean constructorCalled() {
		return constructorCalled;
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

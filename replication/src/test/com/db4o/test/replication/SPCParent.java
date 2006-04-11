/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication;


public class SPCParent {

	private SPCChild child;

	private String name;

	public SPCParent() {

	}

	public SPCParent(String name) {
		this.name = name;
	}

	public SPCParent(SPCChild child, String name) {
		this.child = child;
		this.name = name;
	}

	public SPCChild getChild() {
		return child;
	}

	public void setChild(SPCChild child) {
		this.child = child;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String toString() {
		return "SPCParent{" +
				"child=" + child +
				", name='" + name + '\'' +
				'}';
	}
}

/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.drs.test;

public class Replicated {
	private String name;
	private Replicated link;

	public Replicated() {
	}

	public Replicated(String name) {
		this.setName(name);
	}

	public String toString() {
		return getName() + ", hashcode = " + hashCode()+", identity = "+System.identityHashCode(this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Replicated getLink() {
		return link;
	}

	public void setLink(Replicated link) {
		this.link = link;
	}

	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof Replicated)) return false;
		return ((Replicated)o).name.equals(name);
	}

	public int hashCode() {
		if (name == null) return 0;
		return name.hashCode();
	}
	
	
}

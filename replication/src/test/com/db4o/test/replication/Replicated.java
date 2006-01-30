/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.replication;

public class Replicated {
	public String _name;
	public Replicated _link;

	public Replicated() {
	}

	public Replicated(String name) {
		_name = name;
	}

	public String toString() {
		return _name + ", hashcode = " + hashCode();
	}

}

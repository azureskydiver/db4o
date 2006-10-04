/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.quickstart.simple;


public class Pilot {
	
	public String _name;
	public int _age;

	public Pilot() {
	}

	Pilot(String name, int age) {
		this._name = name;
		this._age = age;
	}
	
    public String toString() {
        return _name + "/" + _age;
    }
}


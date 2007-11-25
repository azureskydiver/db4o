/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.aliases;


public class Driver {
	private String name;
	private int points;
    
    public Driver(String name, int points) {
        this.name=name;
        this.points =points;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name + "/" + points;
    }
}

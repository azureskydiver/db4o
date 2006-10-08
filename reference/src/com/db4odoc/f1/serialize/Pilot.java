/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.serialize;


public class Pilot {
	private String name;
    
    public Pilot(String name) {
        this.name=name;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name){
    	this.name = name;
    }

    public String toString() {
        return name;
    }
}


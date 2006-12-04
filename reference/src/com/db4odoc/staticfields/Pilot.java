/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.staticfields;


public class Pilot {
	private String name;
    private PilotCategories category;

    public Pilot(String name,PilotCategories category) {
        this.name=name;
        this.category=category;
    }

    public PilotCategories getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name+"/"+category;
    }
}

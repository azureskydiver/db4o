/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.refactoring;

public class PilotNew {
	private String identity;
	private int points;
    
    public PilotNew(String name, int points) {
        this.identity=name;
        this.points = points;
    }

    public String getIdentity() {
        return identity;
    }

    public String toString() {
        return identity+"/"+points;
    }
}

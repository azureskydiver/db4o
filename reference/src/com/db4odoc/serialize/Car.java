/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.serialize;

public class Car {
    private String model;
    private Pilot pilot;
    
    public Car(String model, Pilot pilot) {
        this.model=model;
        this.pilot=pilot;
    }

    public Pilot getPilot() {
        return pilot;
    }

    public String getModel() {
        return model;
    }

    public String toString() {
        return model+"["+pilot+"]";
    }
}

/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.android.compare;


public class Car {   
    private String model;
    private Pilot pilot;
    
    public Car(){
    	
    }
    
    public Car(String model) {
        this.model=model;
        this.pilot=null;
    }
      
    public Pilot getPilot() {
        return pilot;
    }
    
    public void setPilot(Pilot pilot) {
        this.pilot = pilot;
    }
    
    public String getModel() {
        return model;
    }
    
    public String toString() {
        return model+"["+pilot+"]";
    }

	public void setModel(String model) {
		this.model = model;
	}
}

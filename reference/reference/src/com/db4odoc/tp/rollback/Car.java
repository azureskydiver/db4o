/* Copyright (C) 2004 - 2008 db4objects Inc. http://www.db4o.com */
package com.db4odoc.tp.rollback;

import com.db4o.activation.*;
import com.db4o.ta.*;

public class Car implements Activatable {
	private String model;
	private Pilot pilot;
	transient Activator _activator;

	public Car(String model, Pilot pilot) {
		this.model = model;
		this.pilot = pilot;
	}
	// end Car

	// Bind the class to an object container
	public void bind(Activator activator) {
    	if (_activator == activator) {
    		return;
    	}
    	if (activator != null && _activator != null) {
            throw new IllegalStateException();
        }
		_activator = activator;
	}
	// end bind

	// activate the object fields
	public void activate(ActivationPurpose purpose) {
		if (_activator == null)
			return;
		_activator.activate(purpose);
	}
	// end activate

	
	public String getModel() {
		activate(ActivationPurpose.READ);
		return model;
	}
	// end getModel

	public void setModel(String model) {
		activate(ActivationPurpose.WRITE);
		this.model = model;
	}
	// end setModel

	public Pilot getPilot() {
		activate(ActivationPurpose.READ);
		return pilot;
	}
	// end getPilot

	public void setPilot(Pilot pilot) {
		activate(ActivationPurpose.WRITE);
		this.pilot = pilot;
	}
	// end setPilot

	public String toString() {
		activate(ActivationPurpose.READ);
		return model + "[" + pilot + "]";
	}
	// end toString

	public void changePilot(String name, int id) {
		pilot.setName(name);
		pilot.setId(id);
	}

}
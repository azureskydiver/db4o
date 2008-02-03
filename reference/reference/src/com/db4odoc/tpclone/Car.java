/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.tpclone;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.ta.Activatable;

public class Car implements Activatable, Cloneable {
	private String model;
	private Pilot pilot;
	transient Activator _activator;

	public Car(String model, Pilot pilot) {
		this.model = model;
		this.pilot = pilot;
	}
	// end Car

	public Object clone() throws CloneNotSupportedException {
		Car test = (Car)super.clone();
		test._activator = null;
		return test;
	}
	// end clone

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

	public String toString() {
		activate(ActivationPurpose.READ);
		return model + "[" + pilot + "]";
	}
	// end toString

}
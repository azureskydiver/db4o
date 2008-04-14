/* Copyright (C) 2008 db4objects Inc. http://www.db4o.com */

package com.db4odoc.tp.rollback;

import com.db4o.activation.*;
import com.db4o.ta.*;

public class Pilot implements Activatable {

	private String name;
	private Id id;

	transient Activator _activator;
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

	// activate the object fields
	public void activate(ActivationPurpose purpose) {
		if (_activator == null)
			return;
		_activator.activate(purpose);
	}


	public Pilot(String name, int id) {
		this.name = name;
		this.id = new Id(id);
	}

	public String getName() {
		activate(ActivationPurpose.READ);
		return name;
	}

	public void setName(String  name) {
		activate(ActivationPurpose.WRITE);
		this.name = name;
	}
	
	public String toString() {
		activate(ActivationPurpose.READ);
		return getName() + "[" + id + "]";
	}

	public void setId(int i) {
		activate(ActivationPurpose.WRITE);
		this.id.change(i);
	}
}

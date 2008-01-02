/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.taexamples;

import com.db4o.activation.*;
import com.db4o.ta.Activatable;

public class Pilot implements Activatable {

	private String _name;

	transient Activator _activator;

	public Pilot(String name) {
		_name = name;
	}

	// Bind the class to an object container
	public void bind(Activator activator) {
		if (null != _activator) {
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

	public String getName() {
		// even simple String needs to be activated
		activate(ActivationPurpose.READ);
		return _name;
	}

	public String toString() {
		// use getName method, which already contains activation call
		return getName();
	}
}

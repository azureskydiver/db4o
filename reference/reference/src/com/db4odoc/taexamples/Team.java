/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.taexamples;

import java.util.*;

import com.db4o.activation.*;
import com.db4o.collections.*;
import com.db4o.ta.*;


public class Team implements Activatable {

	List<Pilot> _pilots = new ArrayList4<Pilot>();
	
	String _name;
	
	//TA Activator
	transient Activator _activator;
	
	//	Bind the class to an object container
	public void bind(Activator activator) {
		if (null != _activator) {
			throw new IllegalStateException();
		}
		_activator = activator;
	}
	
	// activate object fields 
	public void activate(ActivationPurpose purpose) {
		if (_activator == null) return;
		_activator.activate(purpose);
	}
	
	public void addPilot(Pilot pilot) {
		// activate before adding new pilots
		activate(ActivationPurpose.WRITE);
		_pilots.add(pilot);
	}

	public void listAllPilots() {
		// activate before printing the collection members
		activate(ActivationPurpose.READ);
		
		for (Iterator<Pilot> iter = _pilots.iterator(); iter.hasNext();) {
			Pilot pilot = (Pilot) iter.next();
			System.out.println(pilot);
		}
	}
}

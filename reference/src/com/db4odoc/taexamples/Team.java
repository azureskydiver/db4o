/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.taexamples;

import java.util.Iterator;
import java.util.List;

import com.db4o.activation.Activator;
import com.db4o.db4ounit.common.ta.collections.PagedList;
import com.db4o.ta.Activatable;


public class Team implements Activatable {

	List<Pilot> _pilots = new PagedList();
	
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
	public void activate() {
		if (_activator == null) return;
		_activator.activate();
	}
	
	public void addPilot(Pilot pilot) {
		_pilots.add(pilot);
	}

	public void listAllPilots() {
		// activate before printing the collection members
		activate();
		
		for (Iterator<Pilot> iter = _pilots.iterator(); iter.hasNext();) {
			Pilot pilot = (Pilot) iter.next();
			System.out.println(pilot);
		}
	}
}

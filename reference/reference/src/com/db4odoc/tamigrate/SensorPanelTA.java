/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.tamigrate;

import com.db4o.activation.*;
import com.db4o.ta.*;

public class SensorPanelTA /*must implement Activatable for TA*/implements Activatable {

	private Object _sensor;

	private SensorPanelTA _next;

	/*activator registered for this class*/
	transient Activator _activator;
	
	public SensorPanelTA() {
		// default constructor for instantiation
	}
	// end SensorPanelTA

	public SensorPanelTA(int value) {
		_sensor = new Integer(value);
	}
	// end SensorPanelTA

	/*Bind the class to the specified object container, create the activator*/
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

	/*Call the registered activator to activate the next level,
	 * the activator remembers the objects that were already 
	 * activated and won't activate them twice. 
	 */
	public void activate(ActivationPurpose purpose) {
		if (_activator == null)
			return;
		_activator.activate(purpose);
	}
	// end activate
	
	public SensorPanelTA getNext() {
		/*activate direct members*/
		activate(ActivationPurpose.READ);
		return _next;
	}
	// end getNext
	
	public Object getSensor() {
		/*activate direct members*/
		activate(ActivationPurpose.READ);
		return _sensor;
	}
	// end getSensor
	
	public SensorPanelTA createList(int length) {
		return createList(length, 1);
	}
	// end createList

	public SensorPanelTA createList(int length, int first) {
		int val = first;
		SensorPanelTA root = newElement(first);
		SensorPanelTA list = root;
		while (--length > 0) {
			list._next = newElement(++val);
			list = list._next;
		}
		return root;
	}
	// end createList

	protected SensorPanelTA newElement(int value) {
		return new SensorPanelTA(value);
	}
	// end newElement

	public String toString() {
		return "Sensor #" + getSensor();
	}
	// end toString
}

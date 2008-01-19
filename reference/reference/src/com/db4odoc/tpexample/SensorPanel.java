/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.tpexample;

import com.db4o.activation.*;
import com.db4o.ta.*;

public class SensorPanel implements Activatable {

	private Object _sensor;

	private SensorPanel _next;

	/*activator registered for this class*/
	transient Activator _activator;
	
	public SensorPanel() {
		// default constructor for instantiation
	}
	// end SensorPanelTA

	public SensorPanel(int value) {
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
	
	public SensorPanel getNext() {
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
	
	public void setSensor(Object sensor) {
		/*activate for persistence*/
		activate(ActivationPurpose.WRITE);
		_sensor = sensor;
	}
	// end setSensor
	
	public SensorPanel createList(int length) {
		return createList(length, 1);
	}
	// end createList

	public SensorPanel createList(int length, int first) {
		int val = first;
		SensorPanel root = newElement(first);
		SensorPanel list = root;
		while (--length > 0) {
			list._next = newElement(++val);
			list = list._next;
		}
		return root;
	}
	// end createList

	protected SensorPanel newElement(int value) {
		return new SensorPanel(value);
	}
	// end newElement

	public String toString() {
		return "Sensor #" + getSensor();
	}
	// end toString
}

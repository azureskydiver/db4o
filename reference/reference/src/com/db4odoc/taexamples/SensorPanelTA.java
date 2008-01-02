/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.taexamples;

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

	public SensorPanelTA(int value) {
		_sensor = new Integer(value);
	}

	/*Bind the class to the specified object container, create the activator*/
	public void bind(Activator activator) {
		if (null != _activator) {
			throw new IllegalStateException();
		}
		_activator = activator;
	}

	/*Call the registered activator to activate the next level,
	 * the activator remembers the objects that were already 
	 * activated and won't activate them twice. 
	 */
	public void activate(ActivationPurpose purpose) {
		if (_activator == null)
			return;
		_activator.activate(purpose);
	}
	
	public SensorPanelTA getNext() {
		/*activate direct members*/
		activate(ActivationPurpose.READ);
		return _next;
	}
	
	public Object getSensor() {
		/*activate direct members*/
		activate(ActivationPurpose.READ);
		return _sensor;
	}
	
	public SensorPanelTA createList(int length) {
		return createList(length, 1);
	}

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

	protected SensorPanelTA newElement(int value) {
		return new SensorPanelTA(value);
	}

	public String toString() {
		return "Sensor #" + getSensor();
	}
}

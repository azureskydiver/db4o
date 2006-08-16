/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.events;

/**
 * Arguments for object related events.
 * 
 * @see EventRegistry
 */
public class ObjectEventArgs extends EventArgs {
	
	private Object _obj;

	/**
	 * Creates a new instance for the specified object.
	 */
	public ObjectEventArgs(Object obj) {
		_obj = obj;
	}

	/**
	 * The object that triggered this event.
	 * 
	 * @property
	 */
	public Object object() {
		return _obj;
	}
}

/* Copyright (C) 2006   Versant Inc.   http://www.db4o.com */

package com.db4o.events;

import com.db4o.internal.*;

/**
 * Arguments for object related events.
 * 
 * @see EventRegistry
 */
public class ObjectEventArgs extends TransactionalEventArgs {
	
	private Object _obj;

	/**
	 * Creates a new instance for the specified object.
	 */
	public ObjectEventArgs(Transaction transaction, Object obj) {
		super(transaction);
		_obj = obj;
	}

	/**
	 * The object that triggered this event.
	 * 
	 * @sharpen.property
	 */
	public Object object() {
		return _obj;
	}
}

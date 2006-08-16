/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.events;

/**
 * Argument for object related events which can be cancelled.
 * 
 * @see EventRegistry
 * @see CancellableEventArgs
 */
public class CancellableObjectEventArgs extends ObjectEventArgs implements CancellableEventArgs {
	private boolean _cancelled;

	/**
	 * Creates a new instance for the specified object.
	 */
	public CancellableObjectEventArgs(Object obj) {
		super(obj);
	}
	
	/**
	 * @see CancellableEventArgs#cancel()
	 */
	public void cancel() {
		_cancelled = true;
	}

	/**
	 * @see CancellableEventArgs#isCancelled()
	 */
	public boolean isCancelled() {
		return _cancelled;
	}
}

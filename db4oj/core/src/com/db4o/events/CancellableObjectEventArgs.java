/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.events;

public class CancellableObjectEventArgs extends ObjectEventArgs implements CancellableEventArgs {
	private boolean _cancelled;

	public CancellableObjectEventArgs(Object obj) {
		super(obj);
	}
	
	public void cancel() {
		_cancelled = true;
	}

	public boolean isCancelled() {
		return _cancelled;
	}
}

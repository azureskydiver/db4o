package com.db4o.events.impl;

import com.db4o.events.CancellableEventArgs;

public class CancellableEventArgsImpl implements CancellableEventArgs {

	private boolean _cancelled;

	public CancellableEventArgsImpl() {
	}

	public void cancel() {
		_cancelled = true;
	}

	public boolean isCancelled() {
		return _cancelled;
	}

}
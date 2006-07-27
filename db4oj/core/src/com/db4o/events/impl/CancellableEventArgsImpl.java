/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.events.impl;

import com.db4o.events.CancellableEventArgs;

/**
 * @exclude
 */
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

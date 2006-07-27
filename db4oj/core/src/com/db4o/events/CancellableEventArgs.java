/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.events;

public interface CancellableEventArgs extends EventArgs {
	public boolean isCancelled();
	public void cancel();
}

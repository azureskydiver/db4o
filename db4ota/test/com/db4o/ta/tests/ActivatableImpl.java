/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.ta.tests;

import com.db4o.activation.*;

public class ActivatableImpl /* TA BEGIN */ implements com.db4o.ta.Activatable /* TA END */{

	// TA BEGIN
	private transient Activator _activator;
	// TA END

	//	 TA BEGIN
	public void bind(Activator activator) {
		if (null != _activator) {
			throw new IllegalStateException();
		}
		_activator = activator;
	}
	
	protected void activate() {
		if (_activator == null) return;
		_activator.activate();
	}
	// TA END
}

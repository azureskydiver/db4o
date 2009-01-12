/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.ta.instrumentation.test.data;

public class ToBeInstrumentedSub extends ToBeInstrumented {

	public void fooSub() {
		int y = _x;
	}

	protected void barSub() {
		int y = _x;
	}

	void bazSub() {
		int y = _x;
	}
	
	private void booSub() {
		int y = _x;
	}

}

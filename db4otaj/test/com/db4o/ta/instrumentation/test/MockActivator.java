package com.db4o.ta.instrumentation.test;

import com.db4o.activation.Activator;

public class MockActivator implements Activator {
	private int _count;
	
	public MockActivator() {
	}
	
	public int count() {
		return _count;
	}

	public void activate()  {
		++_count;
	}

}

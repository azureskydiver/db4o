/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.ta.instrumentation.test;

import com.db4o.activation.*;

public class MockActivator implements Activator {
	private int _count;
	
	public MockActivator() {
	}
	
	public int count() {
		return _count;
	}

	public void activate(ActivationPurpose purpose)  {
		++_count;
	}

}

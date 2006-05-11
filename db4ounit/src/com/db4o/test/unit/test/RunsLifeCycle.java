package com.db4o.test.unit.test;

import com.db4o.test.unit.*;

public class RunsLifeCycle extends TestCase {

	private Exception _exc;
	private boolean _setupCalled=false;
	private boolean _tearDownCalled=false;
	
	public RunsLifeCycle(Exception exc) {
		_exc=exc;
	}
	
	protected void setUp() {
		_setupCalled=true;
	}
	
	protected void tearDown() {
		_tearDownCalled=true;
	}
	
	public boolean setupCalled() {
		return _setupCalled;
	}

	public boolean tearDownCalled() {
		return _tearDownCalled;
	}

	protected void run() throws Exception {
		assertTrue(_setupCalled);
		assertTrue(!_tearDownCalled);
		throw _exc;
	}
}

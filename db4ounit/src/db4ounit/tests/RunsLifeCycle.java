package db4ounit.tests;

import db4ounit.*;

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
		Assert.isTrue(_setupCalled);
		Assert.isTrue(!_tearDownCalled);
		throw _exc;
	}
}

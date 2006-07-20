package db4ounit.tests;

import db4ounit.*;

public class RunsLifeCycle implements TestCase, TestLifeCycle {

	private boolean _setupCalled=false;
	private boolean _tearDownCalled=false;
	
	public void setUp() {
		_setupCalled=true;
	}
	
	public void tearDown() {
		_tearDownCalled=true;
	}
	
	public boolean setupCalled() {
		return _setupCalled;
	}

	public boolean tearDownCalled() {
		return _tearDownCalled;
	}

	public void testMethod() throws Exception {
		Assert.isTrue(_setupCalled);
		Assert.isTrue(!_tearDownCalled);
		throw FrameworkTestCase.EXCEPTION;
	}
}

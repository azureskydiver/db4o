package db4ounit.tests;

import db4ounit.*;

public class RunsWithPluggableSelector extends BaseTestCase {
	public final static String TESTPASS="testPass";
	public final static String TESTFAIL="testFail";
	
	public RunsWithPluggableSelector(String name) {
		super(name);
	}

	public void testPass() {
		Assert.isTrue(true);
	}
	
	public void testFail() throws Exception {
		Assert.fail();
	}
}

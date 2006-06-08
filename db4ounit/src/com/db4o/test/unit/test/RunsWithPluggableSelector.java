package com.db4o.test.unit.test;

import com.db4o.test.unit.*;

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

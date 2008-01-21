/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.tests;

import db4ounit.*;

public class TestSuiteTestCase implements TestCase {
	
	public void testTestsAreDisposedAfterExecution() {
		final Test test = new RunsGreen();
		final TestSuite suite = new TestSuite(test);
		
		ArrayAssert.areEqual(new Test[] { test }, suite.getTests());
		
		suite.run(new TestResult());
		
		Assert.isNull(suite.getTests());
	}

}

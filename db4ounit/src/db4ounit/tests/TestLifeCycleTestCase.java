package db4ounit.tests;

import db4ounit.Assert;
import db4ounit.ReflectionTestSuiteBuilder;
import db4ounit.TestMethod;
import db4ounit.TestSuite;

public class TestLifeCycleTestCase {
	public void testLifeCycle() {
		TestSuite suite = new ReflectionTestSuiteBuilder().fromClass(RunsLifeCycle.class);
		FrameworkTestCase.runTestAndExpect(suite, 1);
		Assert.isTrue(getTestSubject(suite).tearDownCalled());
	}

	private RunsLifeCycle getTestSubject(TestSuite suite) {
		return ((RunsLifeCycle)((TestMethod)suite.getTests()[0]).getSubject());
	}
}

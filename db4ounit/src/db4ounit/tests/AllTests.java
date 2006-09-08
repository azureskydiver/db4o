package db4ounit.tests;

import db4ounit.ReflectionTestSuiteBuilder;
import db4ounit.TestRunner;
import db4ounit.TestSuite;
import db4ounit.TestSuiteBuilder;

public class AllTests implements TestSuiteBuilder {
	
	public TestSuite build() {
		return new ReflectionTestSuiteBuilder(new Class[] {
			FrameworkTestCase.class,
			AssertTestCase.class,
			TestLifeCycleTestCase.class,
			ReflectionTestSuiteBuilderTestCase.class,
			ReinstantiatePerMethodTest.class,
		}).build();
	}
		
	public static void main(String[] args) {
		new TestRunner(AllTests.class).run();
	}	
}

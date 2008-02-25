package db4ounit.tests;

import db4ounit.*;

public class AllTests extends ReflectionTestSuite {
	
	protected Class[] testCases() {
		return new Class[] {
			AssertTestCase.class,
			CompositeTestListenerTestCase.class,
			FrameworkTestCase.class,
			ReflectionTestSuiteBuilderTestCase.class,
			ReinstantiatePerMethodTest.class,
			TestLifeCycleTestCase.class,
			TestRunnerTestCase.class,
			
			db4ounit.tests.fixtures.AllTests.class,
		};
	}
		
	public static void main(String[] args) {
		new ConsoleTestRunner(AllTests.class).run();
	}	
}

package db4ounit.tests;

import com.db4o.foundation.*;

import db4ounit.ReflectionTestSuiteBuilder;
import db4ounit.ConsoleTestRunner;
import db4ounit.TestSuiteBuilder;

public class AllTests implements TestSuiteBuilder {
	
	public Iterator4 iterator() {
		return new ReflectionTestSuiteBuilder(new Class[] {
			FrameworkTestCase.class,
			AssertTestCase.class,
			TestLifeCycleTestCase.class,
			ReflectionTestSuiteBuilderTestCase.class,
			ReinstantiatePerMethodTest.class,
		}).iterator();
	}
		
	public static void main(String[] args) {
		new ConsoleTestRunner(AllTests.class).run();
	}	
}

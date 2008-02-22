package db4ounit.tests;

import com.db4o.foundation.*;

import db4ounit.ReflectionTestSuiteBuilder;
import db4ounit.TestRunner;
import db4ounit.TestSuiteBuilder;

public class AllTests implements TestSuiteBuilder {
	
	public Iterator4 build() {
		return new ReflectionTestSuiteBuilder(new Class[] {
			FrameworkTestCase.class,
			AssertTestCase.class,
			TestLifeCycleTestCase.class,
			TestSuiteTestCase.class,
			ReflectionTestSuiteBuilderTestCase.class,
			ReinstantiatePerMethodTest.class,
		}).build();
	}
		
	public static void main(String[] args) {
		new TestRunner(AllTests.class).run();
	}	
}

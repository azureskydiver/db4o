package db4ounit.tests;

import com.db4o.foundation.*;

import db4ounit.*;

public class TestLifeCycleTestCase implements TestCase {
	public void testLifeCycle() {
		final Iterator4 tests = new ReflectionTestSuiteBuilder(RunsLifeCycle.class).iterator();
		final Test test = (Test)Iterators.next(tests);
		FrameworkTestCase.runTestAndExpect(test, 1);
		
		final RunsLifeCycle testSubject = (RunsLifeCycle)ReflectionTestSuiteBuilder.getTestSubject(test);
		Assert.isTrue(testSubject.tearDownCalled());
	}
}

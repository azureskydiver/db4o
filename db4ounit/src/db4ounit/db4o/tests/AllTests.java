package db4ounit.db4o.tests;

import com.db4o.foundation.Iterator4;

import db4ounit.Assert;
import db4ounit.Test;
import db4ounit.TestFailure;
import db4ounit.TestMethod;
import db4ounit.TestResult;
import db4ounit.TestSuite;
import db4ounit.db4o.Db4oFixture;
import db4ounit.db4o.Db4oTestSuiteBuilder;
import db4ounit.db4o.fixtures.Db4oInMemory;
import db4ounit.db4o.fixtures.Db4oSolo;

public class AllTests {
	public void testSingleTestWithDifferentFixtures() {
		assertSimpleDb4o(new Db4oInMemory());
		assertSimpleDb4o(new Db4oSolo());
	}
	
	public void testMultipleTestsSingleFixture() {
		assertTest(new Db4oTestSuiteBuilder(new Db4oInMemory()).fromClass(MultipleDb4oTestCase.class), 2, false);
	}
	
	private void assertSimpleDb4o(Db4oFixture fixture) {
		TestSuite suite = new Db4oTestSuiteBuilder(fixture).fromClass(SimpleDb4oTestCase.class);
		assertTest(suite, 0);
		Assert.isTrue(getTestSubject(suite).everythingCalled());
	}

	private SimpleDb4oTestCase getTestSubject(TestSuite suite) {
		return ((SimpleDb4oTestCase)((TestMethod)suite.getTests()[0]).getSubject());
	}
	
	private void assertTest(Test test,int expFailures) {
		assertTest(test,expFailures,true);
	}
	
	private void assertTest(Test test,int expFailures,boolean checkExc) {
		TestResult result=new TestResult();
		test.run(result);
		Assert.areEqual(expFailures, result.failures().size());
		if (checkExc) {
			for(Iterator4 iter=result.failures().iterator(); iter.hasNext();) {
				TestFailure failure = (TestFailure) iter.next();
				Assert.areEqual(new RuntimeException(), failure.getFailure());
			}
		}
	}
}

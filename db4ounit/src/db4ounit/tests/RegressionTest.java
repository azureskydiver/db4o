package db4ounit.tests;

import com.db4o.foundation.Iterator4;

import db4ounit.Assert;
import db4ounit.Test;
import db4ounit.TestCase;
import db4ounit.TestFailure;
import db4ounit.TestResult;
import db4ounit.TestSuite;
import db4ounit.TestSuiteBuilder;
import db4ounit.db4o.Db4oFixture;
import db4ounit.db4o.Db4oTestCase;
import db4ounit.db4o.fixtures.Db4oInMemory;
import db4ounit.db4o.fixtures.Db4oSolo;

public class RegressionTest extends TestCase {

	private static class Db4oTestSuiteBuilder extends TestSuiteBuilder {
		protected void configure(TestCase test) {
			((Db4oTestCase)test).fixture(new Db4oInMemory());
		}
	}

	private final static RuntimeException EXCEPTION=new RuntimeException();
	
	protected void run() {
		TestResult result=new TestResult();
		new RunsGreen().run(result);
		Assert.isTrue(result.failures().size() == 0, "not green");
		new RunsRed(EXCEPTION).run(result);
		Assert.isTrue(result.failures().size() == 1, "not red");
		
		assertTest(new RunsGreen(),0);
		assertTest(new RunsRed(EXCEPTION),1);

		assertTest(new TestSuite(new Test[]{new RunsGreen()}),0);
		assertTest(new TestSuite(new Test[]{new RunsRed(EXCEPTION)}),1);
		assertTest(new TestSuite(new Test[]{new RunsGreen(),new RunsRed(EXCEPTION)}),1);
		assertTest(new TestSuite(new Test[]{new RunsRed(EXCEPTION),new RunsRed(EXCEPTION)}),2);
		assertTest(new TestSuite(new Test[]{new RunsRed(EXCEPTION),new RunsGreen()}),1);
		assertTest(new TestSuite(new Test[]{new RunsGreen(),new RunsGreen()}),0);

		TestSuiteBuilder builder=new TestSuiteBuilder();
		
		assertTest(new TestSuite(new Test[]{new RunsWithPluggableSelector(RunsWithPluggableSelector.TESTPASS)}),0,false);
		assertTest(new TestSuite(new Test[]{new RunsWithPluggableSelector(RunsWithPluggableSelector.TESTFAIL)}),1,false);
		assertTest(builder.buildSuite(RunsWithPluggableSelector.class),1,false);

		assertTest(new RunsAssertions(),0);

		RunsLifeCycle runsLifeCycle = new RunsLifeCycle(EXCEPTION);
		assertTest(runsLifeCycle,1);
		Assert.isTrue(runsLifeCycle.tearDownCalled());

		assertSimpleDb4o(new Db4oInMemory());
		assertSimpleDb4o(new Db4oSolo());
		
		assertTest(new Db4oTestSuiteBuilder().buildSuite(MultipleDb4oTestCase.class),2,false);
	}

	private void assertSimpleDb4o(Db4oFixture fixture) {
		SimpleDb4oTestCase simpleDb4o=new SimpleDb4oTestCase();
		simpleDb4o.name("testResultSize");
		simpleDb4o.fixture(fixture);
		assertTest(simpleDb4o,0);
		Assert.isTrue(simpleDb4o.everythingCalled());
	}
	
	private void assertTest(Test test,int expFailures) {
		assertTest(test,expFailures,true);
	}
	
	private void assertTest(Test test,int expFailures,boolean checkExc) {
		TestResult result=new TestResult();
		test.run(result);
		//assertTrue(result.ok()==(expFailures==0));
		Assert.areEqual(expFailures, result.failures().size());
		if (checkExc) {
			for(Iterator4 iter=result.failures().iterator(); iter.hasNext();) {
				TestFailure failure = (TestFailure) iter.next();
				Assert.isTrue(EXCEPTION.equals(failure.getFailure()));
			}
		}
	}
	
	public static void main(String[] args) {
		//new RegressionTest().run();
		TestResult result=new TestResult();
		new RegressionTest().run(result);
		System.out.println(result);
	}
}

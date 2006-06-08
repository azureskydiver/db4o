package com.db4o.test.unit.test;

import com.db4o.foundation.*;
import com.db4o.test.unit.*;
import com.db4o.test.unit.db4o.*;
import com.db4o.test.unit.db4o.fixtures.*;

public class RegressionTest extends TestCase {

	private static class Db4oTestSuiteBuilder extends TestSuiteBuilder {
		protected void configure(TestCase test) {
			((Db4oTestCase)test).fixture(new Db4oInMemory());
		}
	}

	private final static RuntimeException EXC=new RuntimeException();
	
	protected void run() {
		TestResult result=new TestResult();
		new RunsGreen().run(result);
		if(result.failures().size()!=0) {
			throw new AssertionException("not green");
		}
		new RunsRed(EXC).run(result);
		if(result.failures().size()!=1) {
			throw new AssertionException("not red");
		}
		assertTest(new RunsGreen(),0);
		assertTest(new RunsRed(EXC),1);

		assertTest(new TestSuite(new Test[]{new RunsGreen()}),0);
		assertTest(new TestSuite(new Test[]{new RunsRed(EXC)}),1);
		assertTest(new TestSuite(new Test[]{new RunsGreen(),new RunsRed(EXC)}),1);
		assertTest(new TestSuite(new Test[]{new RunsRed(EXC),new RunsRed(EXC)}),2);
		assertTest(new TestSuite(new Test[]{new RunsRed(EXC),new RunsGreen()}),1);
		assertTest(new TestSuite(new Test[]{new RunsGreen(),new RunsGreen()}),0);

		TestSuiteBuilder builder=new TestSuiteBuilder();
		
		assertTest(new TestSuite(new Test[]{new RunsWithPluggableSelector(RunsWithPluggableSelector.TESTPASS)}),0,false);
		assertTest(new TestSuite(new Test[]{new RunsWithPluggableSelector(RunsWithPluggableSelector.TESTFAIL)}),1,false);
		assertTest(builder.buildSuite(RunsWithPluggableSelector.class),1,false);

		assertTest(new RunsAssertions(),0);

		RunsLifeCycle runsLifeCycle = new RunsLifeCycle(EXC);
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
		Assert.isTrue(result.failures().size()==expFailures,"Expected "+expFailures+", but were "+result.failures().size());
		if(checkExc) {
			for(Iterator4 iter=result.failures().iterator();iter.hasNext();) {
				Assert.isTrue(EXC.equals(iter.next()));
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

package db4ounit.tests;

import com.db4o.foundation.Iterator4;

import db4ounit.Assert;
import db4ounit.Test;
import db4ounit.TestFailure;
import db4ounit.TestResult;
import db4ounit.TestSuite;

public class FrameworkTestCase {
	public final static RuntimeException EXCEPTION = new RuntimeException();
	
	public void testRunsGreen() {
		TestResult result = new TestResult();
		new RunsGreen().run(result);
		Assert.isTrue(result.failures().size() == 0, "not green");
	}
	
	public void testRunsRed() {
		TestResult result = new TestResult();
		new RunsRed(EXCEPTION).run(result);
		Assert.isTrue(result.failures().size() == 1, "not red");
	}
	
	public void testTestSuite() {
		runTestAndExpect(new TestSuite(new Test[]{new RunsGreen()}),0);
		runTestAndExpect(new TestSuite(new Test[]{new RunsRed(EXCEPTION)}),1);
		runTestAndExpect(new TestSuite(new Test[]{new RunsGreen(),new RunsRed(EXCEPTION)}),1);
		runTestAndExpect(new TestSuite(new Test[]{new RunsRed(EXCEPTION),new RunsRed(EXCEPTION)}),2);
		runTestAndExpect(new TestSuite(new Test[]{new RunsRed(EXCEPTION),new RunsGreen()}),1);
		runTestAndExpect(new TestSuite(new Test[]{new RunsGreen(),new RunsGreen()}),0);
	}
	
	public static void runTestAndExpect(Test test,int expFailures) {
		runTestAndExpect(test,expFailures,true);
	}
	
	public static void runTestAndExpect(Test test,int expFailures, boolean checkException) {
		TestResult result = new TestResult();
		test.run(result);
		Assert.areEqual(expFailures, result.failures().size());
		if (checkException) {
			for(Iterator4 iter=result.failures().iterator(); iter.hasNext();) {
				TestFailure failure = (TestFailure) iter.next();
				Assert.isTrue(EXCEPTION.equals(failure.getFailure()));
			}
		}
	}
}

package db4ounit.tests;

import db4ounit.*;

public class TestRunner {
	public static void main(String[] args) {
		Test[] tests={new RunsGreen(),new RunsRed(new RuntimeException()),new TestSuiteBuilder().buildSuite(RunsWithPluggableSelector.class),new RunsLifeCycle(new Exception())};
		TestResult result=new TestResult();
		new TestSuite(tests).run(result);
		System.out.println(result);
	}
}

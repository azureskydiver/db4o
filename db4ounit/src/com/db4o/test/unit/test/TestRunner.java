package com.db4o.test.unit.test;

import com.db4o.test.unit.*;

public class TestRunner {
	public static void main(String[] args) {
		Test[] tests={new RunsGreen(),new RunsRed(new RuntimeException()),new TestSuiteBuilder().buildSuite(RunsWithPluggableSelector.class),new RunsLifeCycle(new Exception())};
		TestResult result=new TestResult();
		new TestSuite(tests).run(result);
		System.out.println(result);
	}
}

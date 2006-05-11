package com.db4o.test.unit;

public class TestSuite implements Test {
	Test[] tests;
	
	public TestSuite(Test[] tests) {
		this.tests = tests;
	}

	public void run(TestResult result) {
		for (int i = 0; i < tests.length; i++) {
			tests[i].run(result);
		}
	}
}

package com.db4o.test.unit;

public class TestSuite implements Test {
	private Test[] tests;
	private String name;
	
	public TestSuite(String name, Test[] tests) {
		this.name = name;
		this.tests = tests;
	}
	
	public TestSuite(Test[] tests) {
		this(null, tests);
	}
	
	public String getName() {
		return name == null ? buildSuiteName(tests) : name;
	}

	public void run(TestResult result) {
		for (int i = 0; i < tests.length; i++) {
			tests[i].run(result);
		}
	}
	
	private static String buildSuiteName(Test[] tests) {
		if (tests.length == 0) return "[]";
		
		String firstTestName = tests[0].getName();
		if (tests.length == 1) return "[" + firstTestName + "]";
		
		return "[" + firstTestName + ", ...]";
	}
}

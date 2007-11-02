package com.db4o.instrumentation.test;

import com.db4o.instrumentation.test.core.*;

import db4ounit.*;

public class AllTests implements TestSuiteBuilder {

	public TestSuite build() {
		return new ReflectionTestSuiteBuilder(new Class[] {
				DefaultFilePathRootTestCase.class,
			}).build();	
	}

	public static void main(String[] args) {
		new TestRunner(AllTests.class).run();
	}
}

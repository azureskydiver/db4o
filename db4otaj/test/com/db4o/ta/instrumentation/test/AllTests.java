package com.db4o.ta.instrumentation.test;

import db4ounit.*;

public class AllTests implements TestSuiteBuilder {

	public TestSuite build() {
		return new ReflectionTestSuiteBuilder(new Class[] {
				TransparentActivationClassLoaderTestCase.class,
		}).build();	
	}

	public static void main(String[] args) {
		new TestRunner(AllTests.class).run();
	}

}

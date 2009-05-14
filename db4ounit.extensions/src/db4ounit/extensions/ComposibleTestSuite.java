/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package db4ounit.extensions;

public abstract class ComposibleTestSuite extends Db4oTestSuite {

	protected Class[] composeTests(Class[] testCases) {
		Class[] otherTests = composeWith();
		Class[] composedTests = new Class[otherTests.length + testCases.length];
		System.arraycopy(testCases, 0, composedTests, 0, testCases.length);
		System.arraycopy(otherTests, 0, composedTests, testCases.length, otherTests.length);
		
		return composedTests;
	}
	
	protected Class[] composeWith() {
		return new Class[0];
	}

}

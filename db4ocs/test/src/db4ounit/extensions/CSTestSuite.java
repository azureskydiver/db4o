/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
package db4ounit.extensions;

import db4ounit.*;

public abstract class CSTestSuite implements TestSuiteBuilder {

	public db4ounit.TestSuite build() {
		return new CSTestSuiteBuilder(testCases()).build();
	}
	
	public int run() {
		return new TestRunner(getClass()).run();
	}

	protected abstract Class[] testCases();
}

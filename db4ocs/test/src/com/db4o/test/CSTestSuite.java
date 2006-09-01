/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
package com.db4o.test;

import db4ounit.TestRunner;
import db4ounit.TestSuiteBuilder;

public class CSTestSuite implements TestSuiteBuilder {

	public static void main(String[] args) {
		new TestRunner(CSTestSuite.class).run();
	}

	public db4ounit.TestSuite build() {
		return new CSTestSuiteBuilder(
				new Class[] { 
						FirstConcurrencyTestCase.class 
						}).build();
	}
}

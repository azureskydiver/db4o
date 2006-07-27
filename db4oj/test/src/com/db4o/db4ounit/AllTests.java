/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit;

import db4ounit.TestRunner;
import db4ounit.TestSuite;
import db4ounit.TestSuiteBuilder;
import db4ounit.db4o.Db4oTestCase;
import db4ounit.db4o.Db4oTestSuiteBuilder;
import db4ounit.db4o.fixtures.Db4oSolo;

public class AllTests extends Db4oTestCase implements TestSuiteBuilder {
	
	public static void main(String[] args) {
		new TestRunner(
				new Db4oTestSuiteBuilder(
						new Db4oSolo(),
						AllTests.class)).run();
	}

	public TestSuite build() {
		return new Db4oTestSuiteBuilder(
				fixture(),
				new Class[] {
					EventRegistryTestCase.class,
					QueryStatisticsTestCase.class,
					GlobalLifecycleEventsTestCase.class,
					}).build();
	}
}

/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.tests;

import db4ounit.Assert;
import db4ounit.TestCase;
import db4ounit.TestMethod;
import db4ounit.TestRunner;
import db4ounit.TestSuite;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.tests.FrameworkTestCase;

public class AllTests implements TestCase {
	public static void main(String[] args) {
		new TestRunner(AllTests.class).run();
	}
	
	public void testSingleTestWithDifferentFixtures() {
		assertSimpleDb4o(new Db4oInMemory());
		assertSimpleDb4o(new Db4oSolo());
	}
	
	public void testMultipleTestsSingleFixture() {
		FrameworkTestCase.runTestAndExpect(new Db4oTestSuiteBuilder(new Db4oInMemory(), MultipleDb4oTestCase.class).build(), 2, false);
	}
	
	private void assertSimpleDb4o(Db4oFixture fixture) {
		TestSuite suite = new Db4oTestSuiteBuilder(fixture, SimpleDb4oTestCase.class).build();
		SimpleDb4oTestCase subject = getTestSubject(suite);
		subject.expectedFixture(fixture);
		FrameworkTestCase.runTestAndExpect(suite, 0);		
		Assert.isTrue(subject.everythingCalled());
	}

	private SimpleDb4oTestCase getTestSubject(TestSuite suite) {
		return ((SimpleDb4oTestCase)((TestMethod)suite.getTests()[0]).getSubject());
	}
}

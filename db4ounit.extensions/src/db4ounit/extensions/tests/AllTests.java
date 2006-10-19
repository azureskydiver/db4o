/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.tests;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.tests.*;

public class AllTests implements TestCase {
	private final class ExcludingInMemoryFixture extends Db4oInMemory {
		public ExcludingInMemoryFixture(ConfigurationSource source) {
			super(source);
		}

		public boolean accept(Class clazz) {
			return !OptOutFromTestFixture.class.isAssignableFrom(clazz);
		}
	}

	public static void main(String[] args) {
		new TestRunner(AllTests.class).run();
	}
	
	public void testSingleTestWithDifferentFixtures() {
		ConfigurationSource configSource=new IndependentConfigurationSource();
		assertSimpleDb4o(new Db4oInMemory(configSource));
		assertSimpleDb4o(new Db4oSolo(configSource));
	}
	
	public void testMultipleTestsSingleFixture() {
		MultipleDb4oTestCase.resetConfigureCalls();
		FrameworkTestCase.runTestAndExpect(new Db4oTestSuiteBuilder(new Db4oInMemory(new IndependentConfigurationSource()), MultipleDb4oTestCase.class).build(), 2, false);
		Assert.areEqual(2,MultipleDb4oTestCase.configureCalls());
	}

	public void testSelectiveFixture() {
		Db4oFixture fixture=new ExcludingInMemoryFixture(new IndependentConfigurationSource());
		TestSuite suite = new Db4oTestSuiteBuilder(fixture, new Class[]{AcceptedTestCase.class,NotAcceptedTestCase.class}).build();
		Assert.areEqual(1,suite.getTests().length);
		FrameworkTestCase.runTestAndExpect(suite,0);
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

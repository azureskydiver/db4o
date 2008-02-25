/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.extensions.tests;

import com.db4o.config.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.extensions.mocking.*;

public class FixtureConfigurationTestCase implements TestCase {

	static final class MockFixtureConfiguration
		extends MethodCallRecorder
		implements FixtureConfiguration {
		
		public void configure(Class clazz, Configuration config) {
			record(new MethodCall("configure", clazz, config));
		}

		public String getLabel() {
			return "MOCK";
		}
	}
	
	public static final class TestCase1 extends AbstractDb4oTestCase {
		public void test() {
		}
	}
	
	public static final class TestCase2 extends AbstractDb4oTestCase {
		public void test() {
		}
	}                                              
	
	public void testSolo() {
		assertFixtureConfiguration(new Db4oSolo());
	}
	
	public void testClientServer() {
		assertFixtureConfiguration(
			new Db4oClientServer(
				new GlobalConfigurationSource(),
				false,
				"C/S"));
	}
	
	public void testInMemory() {
		assertFixtureConfiguration(new Db4oInMemory());
	}

	private void assertFixtureConfiguration(Db4oFixture fixture) {
		
		final MockFixtureConfiguration configuration = new MockFixtureConfiguration();
		fixture.fixtureConfiguration(configuration);
		
		Assert.isTrue(
			fixture.getLabel().endsWith(" - " + configuration.getLabel()),
			"FixtureConfiguration label must be part of Fixture label.");
		
		ConsoleTestRunner.runAll(
			new TestResult(),
			new Db4oTestSuiteBuilder(fixture, new Class[] {
				TestCase1.class,
				TestCase2.class,
			}).iterator());
		
		configuration.verify(new MethodCall[] {
			new MethodCall("configure", TestCase1.class, MethodCall.IGNORED_ARGUMENT),
			new MethodCall("configure", TestCase1.class, MethodCall.IGNORED_ARGUMENT),
			new MethodCall("configure", TestCase2.class, MethodCall.IGNORED_ARGUMENT),
			new MethodCall("configure", TestCase2.class, MethodCall.IGNORED_ARGUMENT),
		});
	}
}

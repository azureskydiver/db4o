/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.tests.fixtures;

import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.fixtures.*;
import db4ounit.mocking.*;

public class FixtureBasedTestSuiteTestCase implements TestCase {
	
	static ContextVariable RECORDER_FIXTURE = new ContextVariable();
	
	static ContextVariable FIXTURE1 = new ContextVariable();
	
	static ContextVariable FIXTURE2 = new ContextVariable();
	
	public static final class TestUnit implements TestCase {
		public void testFoo() {
			record("testFoo");
		}
		
		public void testBar() {
			record("testBar");
		}

		private void record(final String test) {
			recorder().record(new MethodCall(test, FIXTURE1.value(), FIXTURE2.value()));
		}

		private MethodCallRecorder recorder() {
			return ((MethodCallRecorder)RECORDER_FIXTURE.value());
		}
	}
	
	public void test() {
		
		final MethodCallRecorder recorder = new MethodCallRecorder();
		
		new TestRunner(new FixtureBasedTestSuite() {
			public FixtureProvider[] fixtureProviders() {
				return new FixtureProvider[] {
					new SimpleFixtureProvider(RECORDER_FIXTURE, new Object[] { recorder }),
					new SimpleFixtureProvider(FIXTURE1, new Object[] { "f11", "f12" }),
					new SimpleFixtureProvider(FIXTURE2, new Object[] { "f21", "f22" }),
				};
			}

			public Class[] testUnits() {
				return new Class[] { TestUnit.class };
			}
		}).run(new TestResult());
		
//		System.out.println(CodeGenerator.generateMethodCallArray(recorder));
		
		recorder.verify(new MethodCall[] {
			new MethodCall("testFoo", new Object[] {"f11", "f21"}),
			new MethodCall("testFoo", new Object[] {"f11", "f22"}),
			new MethodCall("testFoo", new Object[] {"f12", "f21"}),
			new MethodCall("testFoo", new Object[] {"f12", "f22"}),
			new MethodCall("testBar", new Object[] {"f11", "f21"}),
			new MethodCall("testBar", new Object[] {"f11", "f22"}),
			new MethodCall("testBar", new Object[] {"f12", "f21"}),
			new MethodCall("testBar", new Object[] {"f12", "f22"})
		});
	}

}

/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.tests.fixtures;

import db4ounit.*;
import db4ounit.fixtures.*;

public class FixtureContextTestCase implements TestCase {
	
	public static final class ContextRef {
		public FixtureContext value;
	}
	
	public void test() {
		final Fixture f1 = new Fixture();
		final Fixture f2 = new Fixture();
		final ContextRef c1 = new ContextRef();
		final ContextRef c2 = new ContextRef();
		new FixtureContext().run(new Runnable() {
			public void run() {
				f1.with("foo", new Runnable() {
					public void run() {
						assertValue("foo", f1);
						assertNoValue(f2);
						c1.value = FixtureContext.current();
						f2.with("bar", new Runnable() {
							public void run() {
								assertValue("foo", f1);
								assertValue("bar", f2);
								c2.value = FixtureContext.current();
							}
						});
					}
				});
				
			}
		});
		assertNoValue(f1);
		assertNoValue(f2);
		
		c1.value.run(new Runnable() {
			public void run() {
				assertValue("foo", f1);
				assertNoValue(f2);
			}
		});
		
		c2.value.run(new Runnable() {
			public void run() {
				assertValue("foo", f1);
				assertValue("bar", f2);
			}
		});
	}

	private void assertNoValue(final Fixture f1) {
		Assert.expect(IllegalStateException.class, new CodeBlock() {
			public void run() {
				use(f1.value());
			}

			private void use(Object value) {
			}
		});
	}

	private void assertValue(final String expected, final Fixture fixture) {
		Assert.areEqual(expected, fixture.value());
	}

}

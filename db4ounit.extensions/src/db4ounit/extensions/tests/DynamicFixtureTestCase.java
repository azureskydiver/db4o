/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.extensions.tests;

import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;


public class DynamicFixtureTestCase implements TestSuiteBuilder {
	
	public Iterator4 iterator() {
		// The test case simply runs FooTestSuite
		// with a Db4oInMemory fixture to ensure the 
		// the db4o fixture can be successfully propagated
		// to FooTestUnit#test.
		return new Db4oTestSuiteBuilder(
					new Db4oInMemory(),
					FooTestSuite.class).iterator();
	}	
	
	/**
	 * One of the possibly many test units.
	 */
	public static class FooTestUnit extends AbstractDb4oTestCase {
		
		private final Object[] values = MultiValueFixtureProvider.value();
		
		public void test() {
			Assert.isNotNull(db());
			Assert.isNotNull(values);
		}
	}
	
	/**
	 * The test suite which binds together fixture providers and test units.
	 */
	public static class FooTestSuite extends FixtureBasedTestSuite {

		public FixtureProvider[] fixtureProviders() {
			return new FixtureProvider[] {
				
				//
				// IMPORTANT:
				// Db4oFixtureProvider ensures the current db4o fixture is
				// propagated to all generated test cases.
				//
				new Db4oFixtureProvider(),
				
				new MultiValueFixtureProvider(new Object[][] {
					new Object[] { "foo", "bar" },
					new Object[] { new Integer(1), new Integer(42) },
				}),
			};
		}
	
		public Class[] testUnits() {
			return new Class[] {
				FooTestUnit.class,
			};
		}
	}
}

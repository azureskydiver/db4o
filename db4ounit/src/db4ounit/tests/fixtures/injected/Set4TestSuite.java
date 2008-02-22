/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.tests.fixtures.injected;

public class Set4TestSuite {
	
	public Object[] fixtureProviders() {
		return new FixtureProvider[] {
			new CollectionFixtureProvider(),
			new ElementFixtureProvider(),
		};
	}
	
	public Class[] testUnits() { 
		return new Class[] {
			Set4TestCase.class,
		};
	}

}

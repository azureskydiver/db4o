package db4ounit.tests.fixtures.injected;

import db4ounit.tests.fixtures.framework.*;

class CollectionFixtureProvider implements FixtureProvider {
	
	public static final Object TOKEN = new Object();

	public Object[] fixtures() {
		return new Object[] {
			new CollectionSet4(),
			new HashtableSet4(),
		};
	}
}
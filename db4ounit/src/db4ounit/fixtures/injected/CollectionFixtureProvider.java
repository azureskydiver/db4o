package db4ounit.fixtures.injected;

import db4ounit.fixtures.tests.*;

class CollectionFixtureProvider implements FixtureProvider {
	
	public static final Object TOKEN = new Object();

	public Object[] fixtures() {
		return new Object[] {
			new CollectionSet4(),
			new HashtableSet4(),
		};
	}
}
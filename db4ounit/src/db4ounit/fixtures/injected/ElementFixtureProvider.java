package db4ounit.fixtures.injected;

class ElementFixtureProvider implements FixtureProvider {
	
	public static final Object TOKEN = new Object();
	
	public Object[] fixtures() {
		return new Object[] {
			new Object[] {},
			new Object[] { "foo", "bar", "baz" },
			new Object[] { "foo" },
			new Object[] { new Integer(42), new Integer(-1) }
		};
	}
}
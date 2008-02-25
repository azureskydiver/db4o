package db4ounit.fixtures.injected;

import db4ounit.*;
import db4ounit.fixtures.tests.*;

public class Set4TestCase extends FixtureSensitiveImpl implements TestLifeCycle {
	
	public void setUp() {
		Set4 set = setFixture();
		Object[] data = dataFixture();
		for (int i=0; i<data.length; ++i) {
			Object element = data[i];
			set.add(element);
		}
	}
	
	public void testSize() {
		Assert.areEqual(dataFixture().length, setFixture().size());
	}
	
	public void testContains() {
		final Set4 set = setFixture();
		Object[] data = dataFixture();
		for (int i=0; i<data.length; ++i) {
			Object element = data[i];
			Assert.isTrue(set.contains(element));
		}
	}

	private Object[] dataFixture() {
		return (Object[])fixture(ElementFixtureProvider.TOKEN);
	}

	private Set4 setFixture() {
		return (Set4)fixture(CollectionFixtureProvider.TOKEN);
	}

	public void tearDown() throws Exception {
	}
}
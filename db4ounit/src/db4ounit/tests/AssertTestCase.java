package db4ounit.tests;

import db4ounit.*;

public class AssertTestCase {
	public void testAreEqual() {
		Assert.areEqual(true, true);
		Assert.areEqual(42, 42);
		Assert.areEqual(new Integer(42), new Integer(42));
		try {
			Assert.areEqual(true, false);
			Assert.fail();
		}
		catch (AssertionException e) {
			// OK
		}
		try {
			Assert.areEqual(42, 43);
			Assert.fail();
		}
		catch (AssertionException e) {
			// OK
		}
		try {
			Assert.areEqual(new Object(), new Object());
			Assert.fail();
		}
		catch (AssertionException e) {
			// OK
		}
	}
}

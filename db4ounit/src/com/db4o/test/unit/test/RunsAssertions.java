package com.db4o.test.unit.test;

import com.db4o.test.unit.*;

public class RunsAssertions extends TestCase {
	protected void run() throws Exception {
		Assert.areEqual(true, true);
		Assert.areEqual(42,42);
		Assert.areEqual(new Integer(42),new Integer(42));
		try {
			Assert.areEqual(true,false);
			Assert.fail();
		}
		catch(AssertionException exc) {
			// OK
		}
		try {
			Assert.areEqual(42,43);
			Assert.fail();
		}
		catch(AssertionException exc) {
			// OK
		}
		try {
			Assert.areEqual(new Object(),new Object());
			Assert.fail();
		}
		catch(AssertionException exc) {
			// OK
		}
	}
}

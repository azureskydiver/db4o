package com.db4o.test.unit.test;

import com.db4o.test.unit.*;

public class RunsAssertions extends TestCase {
	protected void run() throws Exception {
		assertEquals(true, true);
		assertEquals(42,42);
		assertEquals(new Integer(42),new Integer(42));
		try {
			assertEquals(true,false);
			fail();
		}
		catch(AssertionException exc) {
			// OK
		}
		try {
			assertEquals(42,43);
			fail();
		}
		catch(AssertionException exc) {
			// OK
		}
		try {
			assertEquals(new Object(),new Object());
			fail();
		}
		catch(AssertionException exc) {
			// OK
		}
	}
}

package com.db4o.test.unit;

public class Assert {
	public static void fail() {
		fail("Assertion failed");
	}

	public static void fail(String msg) {
		throw new AssertionException(msg);
	}
	
	public static void assertTrue(boolean exp) {
		assertTrue(exp,"FAILURE");
	}

	public static void assertTrue(boolean exp,String msg) {
		if(!exp) {
			fail(msg);
		}
	}
	
	public static void assertEquals(boolean exp,boolean actual) {
		if(exp!=actual) {
			fail("Expected "+exp+" but was "+actual);
		}
	}

	public static void assertEquals(int exp,int actual) {
		if(exp!=actual) {
			fail("Expected "+exp+" but was "+actual);
		}
	}

	public static void assertEquals(Object exp,Object actual) {
		if(!exp.equals(actual)) {
			fail("Expected "+exp+" but was "+actual);
		}
	}
}

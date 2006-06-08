package com.db4o.test.unit;

public final class Assert {
	public static void fail() {
		fail("Assertion failed");
	}

	public static void fail(String msg) {
		throw new AssertionException(msg);
	}
	
	public static void isTrue(boolean exp) {
		isTrue(exp,"FAILURE");
	}

	public static void isTrue(boolean exp,String msg) {
		if(!exp) {
			fail(msg);
		}
	}
	
	public static void areEqual(boolean exp,boolean actual) {
		if(exp!=actual) {
			fail("Expected "+exp+" but was "+actual);
		}
	}

	public static void areEqual(int exp,int actual) {
		if(exp!=actual) {
			fail("Expected "+exp+" but was "+actual);
		}
	}

	public static void areEqual(Object exp,Object actual) {
		if(!exp.equals(actual)) {
			fail("Expected "+exp+" but was "+actual);
		}
	}
}

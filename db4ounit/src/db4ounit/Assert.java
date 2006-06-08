package db4ounit;

public final class Assert {
	public static void fail() {
		fail("Assertion failed");
	}

	public static void fail(String msg) {
		throw new AssertionException(msg);
	}
	
	public static void isTrue(boolean condition) {
		isTrue(condition,"FAILURE");
	}

	public static void isTrue(boolean condition, String msg) {
		if (condition) return;
		fail(msg);
	}
	
	public static void areEqual(boolean expected, boolean actual) {
		if(expected == actual) return;
		fail("Expected '"+ expected + "' but was '"+ actual + "'");
	}

	public static void areEqual(int expected, int actual) {
		if (expected == actual) return;
		fail("Expected '"+ expected + "' but was '" + actual + "'");
	}

	public static void areEqual(Object expected, Object actual) {
		if(expected.equals(actual)) return;
		fail("Expected '"+ expected + "' but was '" + actual + "'");
	}
}

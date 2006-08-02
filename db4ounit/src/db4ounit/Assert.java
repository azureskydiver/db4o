package db4ounit;

public final class Assert {
	
	public static void expect(Class exception, CodeBlock block) {
		try {
			block.run();
		} catch (Exception e) {
			if (exception.isInstance(e)) return;
			fail("Expecting '" + exception.getName() + "' but got '" + e.getClass().getName() + "'");
		}
		fail("Exception '" + exception.getName() + "' expected");
	}
	
	public static void fail() {
		fail("FAILURE");
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
	
	public static void isNull(Object reference) {
		if (reference != null) {
			fail("FAILURE");
		}
	}
	
	public static void isNotNull(Object reference) {
		if (reference == null) {
			fail("FAILURE");
		}
	}
	
	public static void areEqual(boolean expected, boolean actual) {
		if (expected == actual) return;
		fail(failureMessage(new Boolean(expected), new Boolean(actual)));
	}

	public static void areEqual(int expected, int actual) {
		if (expected == actual) return;
		fail(failureMessage(new Integer(expected), new Integer(actual)));
	}
	
	public static void areEqual(int expected, int actual, String message) {
		if (expected == actual) return;
		fail(message);
	}
	
	public static void areEqual(long expected, long actual) {
		if (expected == actual) return;
		fail(failureMessage(new Long(expected), new Long(actual)));
	}

	public static void areEqual(Object expected, Object actual) {		
		if (objectsAreEqual(expected, actual)) return;
		fail(failureMessage(expected, actual));
	}

	public static void areSame(Object expected, Object actual) {
		if (expected == actual) return;
		fail(failureMessage(expected, actual));
	}
	
	public static void areNotSame(Object expected, Object actual) {
		if (expected != actual) return;
		fail("Expecting not '" + expected + "'.");
	}
	
	private static String failureMessage(Object expected, Object actual) {
		return "Expected '"+ expected + "' but was '" + actual + "'";
	}
	
	private static boolean objectsAreEqual(Object expected, Object actual) {
		return expected == actual
			|| (expected != null
				&& actual != null
				&& expected.equals(actual));
	}

	public static void isFalse(boolean condition) {
		isTrue(!condition);
	}		
}

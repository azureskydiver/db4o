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
		isNull(reference, "Expected reference to be null, but was "+reference);
	}

	public static void isNull(Object reference,String message) {
		if (reference != null) {
			fail(message);
		}
	}

	public static void isNotNull(Object reference) {
		isNotNull(reference, failureMessage("not null", reference));
	}

	public static void isNotNull(Object reference,String message) {
		if (reference == null) {
			fail(message);
		}
	}

	public static void areEqual(boolean expected, boolean actual) {
		if (expected == actual) return;
		fail(failureMessage(new Boolean(expected), new Boolean(actual)));
	}

	public static void areEqual(int expected, int actual) {
		areEqual(expected,actual,null);
	}
	
	public static void areEqual(int expected, int actual, String message) {
		if (expected == actual) return;
		fail(failureMessage(new Integer(expected), new Integer(actual),message));
	}
	
	public static void areEqual(double expected, double actual) {
		if (expected == actual) return;
		fail(failureMessage(new Double(expected), new Double(actual)));
	}
	
	public static void areEqual(long expected, long actual) {
		if (expected == actual) return;
		fail(failureMessage(new Long(expected), new Long(actual)));
	}

	public static void areEqual(Object expected, Object actual,String message) {		
		if (objectsAreEqual(expected, actual)) return;
		fail(failureMessage(expected, actual, message));
	}
	
	public static void areEqual(Object expected, Object actual) {		
		areEqual(expected,actual,null);
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
		return failureMessage(expected,actual,null);
	}

	private static String failureMessage(Object expected, Object actual, String customMessage) {
		return failureMessage(expected, actual, "", customMessage);
	}

	private static String failureMessage(Object expected, Object actual, final String cmpOper, String customMessage) {
		return (customMessage==null ? "" : customMessage+": ")+"Expected " + cmpOper + "'"+ expected + "' but was '" + actual + "'";
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
	
	public static void isFalse(boolean condition, String message) {
		isTrue(!condition, message);
	}

	public static void isInstanceOf(Class expectedClass, Object actual) {
		isTrue(expectedClass.isInstance(actual), failureMessage(expectedClass, actual == null ? null : actual.getClass()));
	}

	public static void isGreater(long expected, long actual) {
		if (actual > expected) return;
		fail(failureMessage(new Long(expected), new Long(actual), "greater than ", null));
	}			
	
	public static void isGreaterOrEqual(long expected, long actual) {
		if (actual >= expected) return;
		fail(expected, actual, "greater than or equal to ");
	}

	private static void fail(long expected, long actual, final String operator) {
		fail(failureMessage(new Long(expected), new Long(actual), operator, null));
	}

	public static void areNotEqual(long expected, long actual) {
		if (actual != expected) return;
		fail(expected, actual, "not equal to ");
	}
}

/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit;

/**
 * Utility class to enable the reuse of object comparison and checking
 * methods without asserting.
 */
public class Check {

	public static boolean objectsAreEqual(Object expected, Object actual) {
		return expected == actual
			|| (expected != null
				&& actual != null
				&& expected.equals(actual));
	}

}

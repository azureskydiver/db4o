/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package db4ounit;

import com.db4o.foundation.*;


public class Iterator4Assert {

	public static void areEqual(Iterator4 expected, Iterator4 actual) {
		if (null == expected) {
			Assert.isNull(actual);
			return;
		}
		Assert.isNotNull(actual);		
		while (expected.moveNext()) {
			assertNext(expected.current(), actual);
		}
		if (actual.moveNext()) {
			unexpected(actual.current());
		}
	}

	private static void unexpected(Object element) {
		Assert.fail("Unexpected element: " + element);
	}

	public static void assertNext(final Object expected, Iterator4 iterator) {
		Assert.isTrue(iterator.moveNext(), "'" + expected + "' expected.");
		Assert.areEqual(expected, iterator.current());
	}

	public static void areEqual(Object[] expected, Iterator4 iterator) {
		areEqual(new ArrayIterator4(expected), iterator);
	}

	public static void sameContent(Object[] expected, Iterator4 actual) {
		sameContent(new ArrayIterator4(expected), actual);
	}

	public static void sameContent(Iterator4 expected, Iterator4 actual) {
		final Collection4 allExpected = new Collection4(expected);
		while (actual.moveNext()) {
			final Object current = actual.current();
			final boolean removed = allExpected.remove(current);
			if (! removed) {
				unexpected(current);
			}
		}
		Assert.isTrue(allExpected.isEmpty(), allExpected.toString());
	}

}

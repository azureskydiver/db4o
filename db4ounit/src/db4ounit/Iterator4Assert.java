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
			Assert.fail("Unexpected element: " + actual.current());
		}
	}

	public static void assertNext(final Object expected, Iterator4 iterator) {
		Assert.isTrue(iterator.moveNext(), "'" + expected + "' expected.");
		Assert.areEqual(expected, iterator.current());
	}

	public static void areEqual(Object[] expected, Iterator4 iterator) {
		areEqual(new ArrayIterator4(expected), iterator);
	}

}

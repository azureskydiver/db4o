/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package db4ounit.extensions.foundation;

import com.db4o.foundation.*;

import db4ounit.Assert;

public class Iterator4Assert {

	public static void areEqual(Iterator4 expected, Iterator4 actual) {
		if (null == expected) {
			Assert.isNull(actual);
			return;
		}
		Assert.isNotNull(actual);		
		while (expected.moveNext()) {
			Assert.isTrue(actual.moveNext(), "'" + expected.current() + "' expected.");
			Assert.areEqual(expected.current(), actual.current());
		}
		if (actual.moveNext()) {
			Assert.fail("Unexpected element: " + actual.current());
		}
	}

	public static void areEqual(Object[] expected, Iterator4 iterator) {
		areEqual(new ArrayIterator4(expected), iterator);
	}

}

package com.db4o.db4ounit.foundation;

import com.db4o.foundation.*;

import db4ounit.Assert;

public class IteratorAssert {

	public static void areEqual(Iterator4 expected, Iterator4 actual) {
		if (null == expected) {
			Assert.isNull(actual);
		}
		Assert.isNotNull(actual);		
		while (expected.moveNext()) {
			Assert.isTrue(actual.moveNext(), "'" + expected.current() + "' expected.");
			Assert.areEqual(expected.current(), actual.current());
		}
		Assert.isFalse(actual.moveNext());
	}

}

/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.*;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

/**
 * @exclude
 */
public class YDoubleTestCase extends AbstractDb4oTestCase {
	
	private TypeHandler4 _handler;
	
	protected void db4oSetupBeforeStore() throws Exception {
		_handler = new YDouble(stream());
	}
	
	public void testMarshalling() {
		final Double expected = new Double(1.1);
		
		YapReader buffer = new YapReader(_handler.linkLength());		
		_handler.writeIndexEntry(buffer, expected);
		
		buffer.seek(0);
		final Object actual = _handler.readIndexEntry(buffer);
		Assert.areEqual(expected, actual);
	}

	public void testComparison() {		
		assertComparison(0, 1.1, 1.1);
		assertComparison(1, 1.0, 1.1);
		assertComparison(-1, 1.1, 0.5);
	}

	private void assertComparison(final int expected, final double prepareWith, final double compareTo) {
		_handler.prepareComparison(new Double(prepareWith));		
		final Double doubleCompareTo = new Double(compareTo);
		Assert.areEqual(expected, _handler.compareTo(doubleCompareTo));
		switch (expected) {
		case 0:
			Assert.isTrue(_handler.isEqual(doubleCompareTo));
			Assert.isFalse(_handler.isGreater(doubleCompareTo));
			Assert.isFalse(_handler.isSmaller(doubleCompareTo));
			break;
		case 1:
			Assert.isFalse(_handler.isEqual(doubleCompareTo));
			Assert.isTrue(_handler.isGreater(doubleCompareTo));
			Assert.isFalse(_handler.isSmaller(doubleCompareTo));
			break;
		case -1:
			Assert.isFalse(_handler.isEqual(doubleCompareTo));
			Assert.isFalse(_handler.isGreater(doubleCompareTo));
			Assert.isTrue(_handler.isSmaller(doubleCompareTo));
			break;
		}
		
	}
}

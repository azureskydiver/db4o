/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.foundation;

import com.db4o.foundation.Collection4;
import com.db4o.foundation.Iterator4;
import com.db4o.test.Test;

import db4ounit.Assert;
import db4ounit.TestCase;

public class Collection4TestCase implements TestCase {
	
	public void testFastIterator() {
		Collection4 c = new Collection4();
		
		String[] expected = new String[] { "1", "2", "3" };		
		c.addAll(expected);
		
		Iterator4 iterator = c.iterator();
		Assert.isNotNull(iterator);
		
		for (int i=expected.length-1; i>=0; --i) {
			Test.ensure(iterator.moveNext());
			Test.ensureEquals(expected[i], iterator.current());
		}
		Assert.isFalse(iterator.moveNext());
	}
	
	public void testStrictIterator() {
		Collection4 c = new Collection4();
		
		String[] expected = new String[] { "1", "2", "3" };		
		c.addAll(expected);
		
		Iterator4 iterator = c.strictIterator();
		Assert.isNotNull(iterator);
		
		for (int i=0; i<expected.length; ++i) {
			Assert.isTrue(iterator.moveNext());
			Assert.areEqual(expected[i], iterator.current());
		}
		Assert.isFalse(iterator.moveNext());
	}
}

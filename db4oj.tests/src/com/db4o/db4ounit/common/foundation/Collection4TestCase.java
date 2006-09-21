/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;

public class Collection4TestCase implements TestCase {
	
	public void testFastIterator() {
		Collection4 c = new Collection4();
		
		String[] expected = new String[] { "1", "2", "3" };		
		c.addAll(expected);
		
		Iterator4 iterator = c.iterator();
		Assert.isNotNull(iterator);
		
		for (int i=expected.length-1; i>=0; --i) {
			Assert.isTrue(iterator.moveNext());
			Assert.areEqual(expected[i], iterator.current());
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
	
	public void testToString() {
		Collection4 c = new Collection4();
		Assert.areEqual("[]", c.toString());
		
		c.add("foo");
		Assert.areEqual("[foo]", c.toString());
		c.add("bar");
		Assert.areEqual("[foo, bar]", c.toString());
	}
}

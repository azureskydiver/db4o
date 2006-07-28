/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.foundation;

import com.db4o.foundation.Collection4;
import com.db4o.foundation.Iterator4;
import com.db4o.test.Test;

public class Collection4TestCase {
	
	public void testFastIterator() {
		Collection4 c = new Collection4();
		
		String[] expected = new String[] { "1", "2", "3" };		
		c.addAll(expected);
		
		Iterator4 iterator = c.iterator();
		Test.ensure(null != iterator);
		
		for (int i=expected.length-1; i>=0; --i) {
			Test.ensure(iterator.hasNext());
			Test.ensureEquals(expected[i], iterator.next());
		}
		Test.ensure(!iterator.hasNext());
	}
	
	public void testStrictIterator() {
		Collection4 c = new Collection4();
		
		String[] expected = new String[] { "1", "2", "3" };		
		c.addAll(expected);
		
		Iterator4 iterator = c.strictIterator();
		Test.ensure(null != iterator);
		
		for (int i=0; i<expected.length; ++i) {
			Test.ensure(iterator.hasNext());
			Test.ensureEquals(expected[i], iterator.next());
		}
		Test.ensure(!iterator.hasNext());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Test.run(Collection4TestCase.class);
	}

}

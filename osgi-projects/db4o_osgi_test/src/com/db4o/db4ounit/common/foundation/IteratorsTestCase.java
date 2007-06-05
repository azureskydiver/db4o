/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;

/**
 * @exclude
 */
public class IteratorsTestCase implements TestCase {
	
	public void testFilter() {
		assertFilter(
				new String[] { "bar", "baz" },
				new String[] { "foo", "bar", "baz", "zong" },
				new Predicate4() {
					public boolean match(Object candidate) {
						return ((String)candidate).startsWith("b");
					}});
		
		assertFilter(
				new String[] { "foo", "bar" },
				new String[] { "foo", "bar" },
				new Predicate4() {
					public boolean match(Object candidate) {
						return true;
					}
				});
		
		assertFilter(
				new String[0],
				new String[] { "foo", "bar" },
				new Predicate4() {
					public boolean match(Object candidate) {
						return false;
					}
				});
	}

	private void assertFilter(String[] expected, String[] actual, Predicate4 filter) {
		IteratorAssert.areEqual(expected, Iterators.filter(actual, filter));
	}

	public void testMap() {
		final int[] array = new int[] { 1, 2, 3 };
		final Collection4 args = new Collection4();
		final Iterator4 iterator = Iterators.map(
			IntArrays4.newIterator(array),
			new Function4() {
				public Object apply(Object arg) {
					args.add(arg);
					return new Integer(((Integer)arg).intValue()*2);
				}
			}
		);
		Assert.isNotNull(iterator);
		Assert.areEqual(0, args.size());
		
		for (int i=0; i<array.length; ++i) {
			Assert.isTrue(iterator.moveNext());
			Assert.areEqual(i+1, args.size());
			Assert.areEqual(new Integer(array[i]*2), iterator.current());
		}
	}

}

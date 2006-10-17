/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;

public class Collection4TestCase implements TestCase {
	
	public static void main(String[] args) {
		new TestRunner(Collection4TestCase.class).run();
	}
	
	public void testPrepend() {
		final Collection4 c = new Collection4();
		c.prepend("foo");
		assertCollection(new String[] { "foo" }, c);
		c.add("bar");
		assertCollection(new String[] { "foo", "bar" }, c);
		c.prepend("baz");
		assertCollection(new String[] { "baz", "foo", "bar" }, c);
		c.prepend("gazonk");
		assertCollection(new String[] { "gazonk", "baz", "foo", "bar" }, c);
	}
	
	public void testCopyConstructor() {
		final String[] expected = new String[] { "1", "2", "3" };
		final Collection4 c = newCollection(expected);
		assertCollection(expected, new Collection4(c));
	}
	
	public void testInvalidIteratorException() {
		final Collection4 c = newCollection(new String[] { "1", "2" });
		final Iterator4 i = c.iterator();
		Assert.isTrue(i.moveNext());
		c.add("3");
		Assert.expect(InvalidIteratorException.class, new CodeBlock() {
			public void run() throws Exception {
				bang(i.current());
			}
			
			private void bang(Object o) {
			}
		});
	}
	
	public void testRemove() {
		final Collection4 c = newCollection(new String[] { "1", "2", "3", "4" });
		c.remove("3");
		assertCollection(new String[] { "1", "2", "4"} , c);
		c.remove("4");
		assertCollection(new String[] { "1", "2" } , c);
		c.add("5");
		assertCollection(new String[] { "1", "2", "5" } , c);
		c.remove("1");
		assertCollection(new String[] { "2", "5" } , c);
		c.remove("2");
		c.remove("5");
		assertCollection(new String[] {}, c);
		c.add("6");
		assertCollection(new String[] { "6" }, c);
	}
	
	private void assertCollection(String[] expected, Collection4 c) {
		Assert.areEqual(expected.length, c.size());
		assertIterator(expected, c.iterator());
	}

	public void testIterator() {
		String[] expected = new String[] { "1", "2", "3" };
		Collection4 c = newCollection(expected);		
		assertIterator(expected, c.iterator());
	}	
	
	private Collection4 newCollection(String[] expected) {
		Collection4 c = new Collection4();		
		c.addAll(expected);
		return c;
	}

	private void assertIterator(String[] expected, Iterator4 iterator) {
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

/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.foundation;

import com.db4o.foundation.Hashtable4;
import com.db4o.foundation.Visitor4;

import db4ounit.Assert;
import db4ounit.TestCase;

public class Hashtable4TestCase implements TestCase {
	
	public void testByteArrayKeys() {
		byte[] key1 = new byte[] { 1, 2, 3 };
		byte[] key2 = new byte[] { 3, 2, 1 };
		byte[] key3 = new byte[] { 3, 2, 1 }; // same values as key2
		
		Hashtable4 table = new Hashtable4(2);
		table.put(key1, "foo");
		table.put(key2, "bar");
		
		Assert.areEqual("foo", table.get(key1));
		Assert.areEqual("bar", table.get(key2));
		Assert.areEqual(2, keyCount(table));
		Assert.areEqual(2, table.size());
		
		table.put(key3, "baz");
		Assert.areEqual("foo", table.get(key1));
		Assert.areEqual("baz", table.get(key2));
		Assert.areEqual(2, keyCount(table));
		Assert.areEqual(2, table.size());
		
		Assert.areEqual("baz", table.remove(key2));
		Assert.areEqual(1, keyCount(table));
		Assert.areEqual(1, table.size());
		
		Assert.areEqual("foo", table.remove(key1));
		Assert.areEqual(0, keyCount(table));
		Assert.areEqual(0, table.size());
	}
	
	public void testSameKeyTwice() {
		
		Integer key = new Integer(1);
		
		Hashtable4 table = new Hashtable4();
		table.put(key, "foo");
		table.put(key, "bar");
		
		Assert.areEqual("bar", table.get(key));		
		Assert.areEqual(1, keyCount(table));
	}
	
	class Key {
		int _hashCode;
		
		public Key(int hashCode) {
			_hashCode = hashCode;
		}
		
		public int hashCode() {
			return 0;
		}
	}
	
	public void testDifferentKeysSameHashCode() {
		Key key1 = new Key(1);
		Key key2 = new Key(1);
		Key key3 = new Key(2);
		
		Hashtable4 table = new Hashtable4(2);
		table.put(key1, "foo");
		table.put(key2, "bar");
		
		Assert.areEqual("foo", table.get(key1));
		Assert.areEqual("bar", table.get(key2));
		Assert.areEqual(2, keyCount(table));
		
		table.put(key2, "baz");
		Assert.areEqual("foo", table.get(key1));
		Assert.areEqual("baz", table.get(key2));
		Assert.areEqual(2, keyCount(table));
		
		table.put(key1, "spam");
		Assert.areEqual("spam", table.get(key1));
		Assert.areEqual("baz", table.get(key2));
		Assert.areEqual(2, keyCount(table));
		
		table.put(key3, "eggs");
		Assert.areEqual("spam", table.get(key1));
		Assert.areEqual("baz", table.get(key2));
		Assert.areEqual("eggs", table.get(key3));
		Assert.areEqual(3, keyCount(table));
		
		table.put(key2, "mice");
		Assert.areEqual("spam", table.get(key1));
		Assert.areEqual("mice", table.get(key2));
		Assert.areEqual("eggs", table.get(key3));
		Assert.areEqual(3, keyCount(table));
	}
	
	class KeyCount {
		public int keys;
	}

	private int keyCount(Hashtable4 table) {
		final KeyCount count = new KeyCount();
		table.forEachKey(new Visitor4() {
			public void visit(Object key) {
				++count.keys;
			}
		});
		return count.keys;
	}
}

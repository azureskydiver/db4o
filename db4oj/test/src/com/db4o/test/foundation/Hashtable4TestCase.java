/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.foundation;

import com.db4o.foundation.Hashtable4;
import com.db4o.foundation.Visitor4;
import com.db4o.test.Test;

public class Hashtable4TestCase {
	
	public void testByteArrayKeys() {
		byte[] key1 = new byte[] { 1, 2, 3 };
		byte[] key2 = new byte[] { 3, 2, 1 };
		byte[] key3 = new byte[] { 3, 2, 1 }; // same values as key2
		
		Hashtable4 table = new Hashtable4(2);
		table.put(key1, "foo");
		table.put(key2, "bar");
		
		Test.ensureEquals("foo", table.get(key1));
		Test.ensureEquals("bar", table.get(key2));
		Test.ensureEquals(2, keyCount(table));
		Test.ensureEquals(2, table.size());
		
		table.put(key3, "baz");
		Test.ensureEquals("foo", table.get(key1));
		Test.ensureEquals("baz", table.get(key2));
		Test.ensureEquals(2, keyCount(table));
		Test.ensureEquals(2, table.size());
		
		Test.ensureEquals("baz", table.remove(key2));
		Test.ensureEquals(1, keyCount(table));
		Test.ensureEquals(1, table.size());
		
		Test.ensureEquals("foo", table.remove(key1));
		Test.ensureEquals(0, keyCount(table));
		Test.ensureEquals(0, table.size());
	}
	
	public void testSameKeyTwice() {
		
		Integer key = new Integer(1);
		
		Hashtable4 table = new Hashtable4(1);
		table.put(key, "foo");
		table.put(key, "bar");
		
		Test.ensureEquals("bar", table.get(key));		
		Test.ensureEquals(1, keyCount(table));
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
		
		Test.ensureEquals("foo", table.get(key1));
		Test.ensureEquals("bar", table.get(key2));
		Test.ensureEquals(2, keyCount(table));
		
		table.put(key2, "baz");
		Test.ensureEquals("foo", table.get(key1));
		Test.ensureEquals("baz", table.get(key2));
		Test.ensureEquals(2, keyCount(table));
		
		table.put(key1, "spam");
		Test.ensureEquals("spam", table.get(key1));
		Test.ensureEquals("baz", table.get(key2));
		Test.ensureEquals(2, keyCount(table));
		
		table.put(key3, "eggs");
		Test.ensureEquals("spam", table.get(key1));
		Test.ensureEquals("baz", table.get(key2));
		Test.ensureEquals("eggs", table.get(key3));
		Test.ensureEquals(3, keyCount(table));
		
		table.put(key2, "mice");
		Test.ensureEquals("spam", table.get(key1));
		Test.ensureEquals("mice", table.get(key2));
		Test.ensureEquals("eggs", table.get(key3));
		Test.ensureEquals(3, keyCount(table));
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

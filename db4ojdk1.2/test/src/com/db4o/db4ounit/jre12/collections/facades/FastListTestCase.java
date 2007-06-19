/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections.facades;

import java.util.*;

import com.db4o.collections.facades.*;
import com.db4o.db4ounit.jre12.collections.*;

import db4ounit.*;

public class FastListTestCase implements TestLifeCycle {

	public List _list;

	private static int CAPACITY = 100;

	public static void main(String[] args) {
		new TestRunner(FastListTestCase.class).run();
	}

	public void setUp() throws Exception {
		_list = new FastList(new MockPersistentList());
		 _list = new Vector();
		for (int i = 0; i < CAPACITY; i++) {
			_list.add(new Integer(i));
		}
	}

	public void tearDown() throws Exception {
		// do nothing
	}

	public void testAdd() throws Exception {
		for (int i = 0; i < CAPACITY; ++i) {
			_list.add(new Integer(CAPACITY + i));
		}

		for (int i = 0; i < CAPACITY * 2; ++i) {
			Assert.areEqual(new Integer(i), _list.get(i));
		}
	}

	public void testAdd_LObject() throws Exception {
		Integer i1 = new Integer(0);
		_list.add(0, i1);
		// elements: 0, 0,1 - 100
		// index: 0, 1,2 - 101
		Assert.areSame(i1, _list.get(0));

		for (int i = 1; i < CAPACITY + 1; ++i) {
			Assert.areEqual(new Integer(i - 1), _list.get(i));
		}

		Integer i2 = new Integer(42);
		_list.add(42, i2);
		// elements: 0, 0,1 - 42, 42, 43 - 100
		// index: 0, 1,2 - 43, 44, 45 - 102
		for (int i = 1; i < 42; ++i) {
			Assert.areEqual(new Integer(i - 1), _list.get(i));
		}

		Assert.areSame(i2, _list.get(42));
		Assert.areEqual(new Integer(41), _list.get(43));

		for (int i = 44; i < CAPACITY + 2; ++i) {
			Assert.areEqual(new Integer(i - 2), _list.get(i));
		}

	}

	public void testAddAll_LCollection() throws Exception {
		Vector v = new Vector();
		for (int i = 0; i < CAPACITY; ++i) {
			v.add(new Integer(CAPACITY + i));
		}

		_list.addAll(v);

		for (int i = 0; i < CAPACITY * 2; ++i) {
			Assert.areEqual(new Integer(i), _list.get(i));
		}
	}

	public void testAddAll_ILCollection() throws Exception {
		final Vector v = new Vector();
		final int INDEX = 42;

		for (int i = 0; i < CAPACITY; ++i) {
			v.add(new Integer(CAPACITY + i));
		}

		_list.addAll(INDEX, v);
		// elements: 0 - 41, 100 - 199, 42 - 100
		// index: 0 - 41, 42 - 141, 142 - 200
		for (int i = 0; i < INDEX; ++i) {
			Assert.areEqual(new Integer(i), _list.get(i));
		}

		for (int i = INDEX, j = 0; j < CAPACITY; ++i, ++j) {
			Assert.areEqual(new Integer(CAPACITY + j), _list.get(i));
		}

		for (int i = INDEX + CAPACITY; i < CAPACITY * 2; ++i) {
			Assert.areEqual(new Integer(i - CAPACITY), _list.get(i));
		}

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {

			public void run() throws Throwable {
				_list.addAll(-1, v);
			}

		});
	}

	public void testClear() throws Exception {
		_list.clear();
		Assert.areEqual(0, _list.size());
	}

	public void testContains() throws Exception {
		Assert.isTrue(_list.contains(new Integer(0)));
		Assert.isTrue(_list.contains(new Integer(CAPACITY / 2)));
		Assert.isTrue(_list.contains(new Integer(CAPACITY / 3)));
		Assert.isTrue(_list.contains(new Integer(CAPACITY / 4)));

		Assert.isFalse(_list.contains(new Integer(-1)));
		Assert.isFalse(_list.contains(new Integer(CAPACITY)));

		// returns false because current data doesn't contain null.
		// Quotes from j.u.List spec: More formally, returns true if and only if
		// this list contains at least one element e such that (o==null ?
		// e==null : o.equals(e)).
		Assert.isFalse(_list.contains(null));
	}
	
	public void testContainsAll() throws Exception {
		Vector v = new Vector();
		
		v.add(new Integer(0));
		Assert.isTrue(_list.containsAll(v));
		
		v.add(new Integer(0));
		Assert.isTrue(_list.containsAll(v));
		
		v.add(new Integer(CAPACITY / 2));
		Assert.isTrue(_list.containsAll(v));
		
		v.add(new Integer(CAPACITY / 3));
		Assert.isTrue(_list.containsAll(v));
		
		v.add(new Integer(CAPACITY / 4));
		Assert.isTrue(_list.containsAll(v));
		
		v.add(new Integer(CAPACITY));
		Assert.isFalse(_list.containsAll(v));		
	}
	
	public void testGet() throws Exception {
		for (int i = 0; i < CAPACITY; ++i) {
			Assert.areEqual(new Integer(i), _list.get(i));
		}
	}

}

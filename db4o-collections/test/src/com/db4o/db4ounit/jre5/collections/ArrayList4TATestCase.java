/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections;

import java.util.*;

import com.db4o.collections.*;
import com.db4o.config.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @exclude
 */
public class ArrayList4TATestCase extends AbstractDb4oTestCase {

	private static int CAPACITY = 100;

	public static void main(String[] args) {
		new ArrayList4TATestCase().runSolo();
	}


	@Override
	protected void store() throws Exception {
		ArrayList4<Integer> list = new ArrayList4<Integer>();
		for (int i = 0; i < CAPACITY; i++) {
			list.add(new Integer(i));
		}
		store(list);
	}

	@Override
	protected void configure(Configuration config) throws Exception {
		config.add(new TransparentActivationSupport());
		config.activationDepth(0);
		super.configure(config);
	}

	@SuppressWarnings("unchecked")
	public void testGet_I() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		for (int i = 0; i < CAPACITY; ++i) {
			Assert.areEqual(new Integer(i), list.get(i));
		}

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.get(-1);
			}
		});

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.get(CAPACITY);
			}
		});
	}

	public void testAdd() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		for (int i = 0; i < CAPACITY; ++i) {
			list.add(new Integer(CAPACITY + i));
		}

		for (int i = 0; i < CAPACITY * 2; ++i) {
			Assert.areEqual(new Integer(i), list.get(i));
		}
	}

	public void testAdd_LObject() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.add(-1, new Integer(0));
			}
		});

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.add(CAPACITY + 1, new Integer(0));
			}
		});

		Integer i1 = new Integer(0);
		list.add(0, i1);
		// elements: 0, 0,1 - 100
		// index: 0, 1,2 - 101
		Assert.areSame(i1, list.get(0));

		for (int i = 1; i < CAPACITY + 1; ++i) {
			Assert.areEqual(new Integer(i - 1), list.get(i));
		}

		Integer i2 = new Integer(42);
		list.add(42, i2);
		// elements: 0, 0,1 - 42, 42, 43 - 100
		// index: 0, 1,2 - 43, 44, 45 - 102
		for (int i = 1; i < 42; ++i) {
			Assert.areEqual(new Integer(i - 1), list.get(i));
		}

		Assert.areSame(i2, list.get(42));
		Assert.areEqual(new Integer(41), list.get(43));

		for (int i = 44; i < CAPACITY + 2; ++i) {
			Assert.areEqual(new Integer(i - 2), list.get(i));
		}
	}

	public void testAddAll_LCollection() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		final Vector<Integer> v = new Vector<Integer>();
		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.addAll(-1, v);
			}
		});

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.addAll(CAPACITY + 1, v);
			}
		});

		for (int i = 0; i < CAPACITY; ++i) {
			v.add(new Integer(CAPACITY + i));
		}

		list.addAll(v);

		for (int i = 0; i < CAPACITY * 2; ++i) {
			Assert.areEqual(new Integer(i), list.get(i));
		}
	}

	public void testAddAll_ILCollection() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		final Vector<Integer> v = new Vector<Integer>();
		final int INDEX = 42;

		for (int i = 0; i < CAPACITY; ++i) {
			v.add(new Integer(CAPACITY + i));
		}

		list.addAll(INDEX, v);
		// elements: 0 - 41, 100 - 199, 42 - 100
		// index: 0 - 41, 42 - 141, 142 - 200
		for (int i = 0; i < INDEX; ++i) {
			Assert.areEqual(new Integer(i), list.get(i));
		}

		for (int i = INDEX, j = 0; j < CAPACITY; ++i, ++j) {
			Assert.areEqual(new Integer(CAPACITY + j), list.get(i));
		}

		for (int i = INDEX + CAPACITY; i < CAPACITY * 2; ++i) {
			Assert.areEqual(new Integer(i - CAPACITY), list.get(i));
		}

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.addAll(-1, v);
			}
		});
	}

	public void testClear() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		list.clear();
		Assert.areEqual(0, list.size());
	}
	
	public void testIsEmpty() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		Assert.isTrue(new ArrayList4<Integer>().isEmpty());
		Assert.isFalse(list.isEmpty());
		list.clear();
		Assert.isTrue(list.isEmpty());
	}

	public void testSize() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		Assert.areEqual(CAPACITY, list.size());
		for (int i = 0; i < CAPACITY; ++i) {
			list.remove(0);
			Assert.areEqual(CAPACITY - 1 - i, list.size());
		}
		for (int i = 0; i < CAPACITY; ++i) {
			list.add(new Integer(i));
			Assert.areEqual(i + 1, list.size());
		}
	}


	@SuppressWarnings("unchecked")
	private ArrayList4<Integer> retrieveAndAssertNullArrayList4() {
		ArrayList4<Integer> list = (ArrayList4<Integer>) retrieveOnlyInstance(ArrayList4.class);
		assertNullArrayList4(list);
		return list;
	}

	private void assertNullArrayList4(ArrayList4<Integer> list) {
		Assert.isNull(list.elements);
		Assert.areEqual(0, list.capacity);
		Assert.areEqual(0, list.listSize);
	}
}

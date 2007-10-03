/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections;

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
		final ArrayList4<Integer> list = retrieveArrayList4();
		assertNullArrayList4(list);
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

	public void testSize() throws Exception {
		final ArrayList4<Integer> list = retrieveArrayList4();
		assertNullArrayList4(list);
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
	private ArrayList4<Integer> retrieveArrayList4() {
		return (ArrayList4<Integer>) retrieveOnlyInstance(ArrayList4.class);
	}

	private void assertNullArrayList4(ArrayList4<Integer> list) {
		Assert.isNull(list.elements);
		Assert.areEqual(0, list.capacity);
		Assert.areEqual(0, list.listSize);
	}
}

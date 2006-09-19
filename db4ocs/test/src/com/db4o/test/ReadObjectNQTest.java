/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.List;

import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Predicate;
import com.db4o.test.config.Configure;
import com.db4o.test.data.SimpleObject;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class ReadObjectNQTest extends ClientServerTestCase {

	private static String testString = "simple test string";

	protected void store(ExtObjectContainer oc) throws Exception {
		for (int i = 0; i < Configure.CONCURRENCY_THREAD_COUNT; i++) {
			oc.set(new SimpleObject(testString + i, i));
		}
	}

	public void concReadSameObject(ExtObjectContainer oc) throws Exception {
		int mid = Configure.CONCURRENCY_THREAD_COUNT / 2;
		final SimpleObject expected = new SimpleObject(testString + mid, mid);
		List<SimpleObject> result = oc.query(new Predicate<SimpleObject>() {
			public boolean match(SimpleObject o) {
				return expected.equals(o);
			}
		});
		Assert.areEqual(1, result.size());
		Assert.areEqual(expected, result.get(0));
		System.out.println("AAAAAAAA");
	}

	public void concReadDifferentObject(ExtObjectContainer oc, int seq)
			throws Exception {
		final SimpleObject expected = new SimpleObject(testString + seq, seq);
		List<SimpleObject> result = oc.query(new Predicate<SimpleObject>() {
			public boolean match(SimpleObject o) {
				return expected.equals(o);
			}
		});
		Assert.areEqual(1, result.size());
		Assert.areEqual(expected, result.get(0));
		System.out.println("BBBBBBBB");
	}

}

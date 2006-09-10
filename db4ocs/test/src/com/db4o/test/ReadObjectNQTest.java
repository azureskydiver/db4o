/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.List;

import com.db4o.query.Predicate;
import com.db4o.test.config.Configure;
import com.db4o.test.data.SimpleObject;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class ReadObjectNQTest extends ClientServerTestCase {

	private String testString = "simple test string";

	protected void store() {
		int total = Configure.CONCURRENCY_THREAD_COUNT;
		oc = server.openClient();
		for (int i = 0; i < total; i++) {
			oc.set(new SimpleObject(testString + i, i));
		}
		oc.close();
	}

	public void concReadSameObject() throws Exception {
		oc = server.openClient();
		int mid = Configure.CONCURRENCY_THREAD_COUNT / 2;
		final SimpleObject expected = new SimpleObject(testString + mid, mid);
		List<SimpleObject> result = oc.query(new Predicate<SimpleObject>() {
			public boolean match(SimpleObject o) {
				return expected.equals(o);
			}
		});
		Assert.areEqual(1, result.size());
		Assert.areEqual(expected, result.get(0));
	}

	public void concReadDifferentObject(int seq) throws Exception {
		oc = server.openClient();
		final SimpleObject expected = new SimpleObject(testString + seq, seq);
		List<SimpleObject> result = oc.query(new Predicate<SimpleObject>() {
			public boolean match(SimpleObject o) {
				return expected.equals(o);
			}
		});
		Assert.areEqual(1, result.size());
		Assert.areEqual(expected, result.get(0));
	}

	public void tearDown() throws Exception {
		oc.close();
		super.tearDown();
	}

}

/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.*;

import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.test.config.*;
import com.db4o.test.persistent.*;

import db4ounit.*;

public class ReadObjectNQTest extends ClientServerTestCase {

	private static String testString = "simple test string";

	protected void store(ExtObjectContainer oc) throws Exception {
		for (int i = 0; i < TestConfigure.CONCURRENCY_THREAD_COUNT; i++) {
			oc.set(new SimpleObject(testString + i, i));
		}
	}

	public void concReadSameObject(ExtObjectContainer oc) throws Exception {
		int mid = TestConfigure.CONCURRENCY_THREAD_COUNT / 2;
		final SimpleObject expected = new SimpleObject(testString + mid, mid);
		List<SimpleObject> result = oc.query(new MyPredicate(expected));
		Assert.areEqual(1, result.size());
		Assert.areEqual(expected, result.get(0));
	}

	public void concReadDifferentObject(ExtObjectContainer oc, int seq)
			throws Exception {
		final SimpleObject expected = new SimpleObject(testString + seq, seq);
		List<SimpleObject> result = oc.query(new MyPredicate(expected));
		Assert.areEqual(1, result.size());
		Assert.areEqual(expected, result.get(0));
	}

}

class MyPredicate extends Predicate <SimpleObject>{
	SimpleObject expected;

	MyPredicate(SimpleObject o) {
		this.expected = o;
	}

	public boolean match(SimpleObject candidate) {
		return expected.equals(candidate);
	}

}

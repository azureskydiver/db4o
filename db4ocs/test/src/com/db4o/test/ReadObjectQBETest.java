/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.test.config.*;
import com.db4o.test.persistent.*;

import db4ounit.*;

public class ReadObjectQBETest extends ClientServerTestCase {

	private static String testString = "simple test string";

	protected void store(ExtObjectContainer oc) {
		for (int i = 0; i < TestConfigure.CONCURRENCY_THREAD_COUNT; i++) {
			oc.set(new SimpleObject(testString + i, i));
		}
	}

	public void concReadSameObject(ExtObjectContainer oc) throws Exception {
		int mid = TestConfigure.CONCURRENCY_THREAD_COUNT / 2;
		SimpleObject example = new SimpleObject(testString + mid, mid);
		ObjectSet result = oc.get(example);
		Assert.areEqual(1, result.size());
		Assert.areEqual(example, result.next());
	}

	public void concReadDifferentObject(ExtObjectContainer oc, int seq)
			throws Exception {

		SimpleObject example = new SimpleObject(testString + seq, seq);
		ObjectSet result = oc.get(example);
		Assert.areEqual(1, result.size());
		Assert.areEqual(example, result.next());

	}

}

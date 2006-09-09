/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.test.config.Configure;
import com.db4o.test.data.SimpleObject;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class ReadObjectQBETest extends ClientServerTestCase {

	ObjectContainer oc;

	private String testString = "simple test string";

	protected void store() {
		oc = openClient();
		int total = Configure.CONCURRENCY_THREAD_COUNT;
		for (int i = 0; i < total; i++) {
			oc.set(new SimpleObject(testString + i, i));
		}
	}

	public void tearDown() throws Exception {
		oc.close();
		super.tearDown();
	}

	public void concReadSameObject() throws Exception {
		int mid = Configure.CONCURRENCY_THREAD_COUNT / 2;
		SimpleObject example = new SimpleObject(testString + mid, mid);
		ObjectSet result = oc.get(example);
		Assert.areEqual(1, result.size());
		Assert.areEqual(example, result.next());
	}

	public void concReadDifferentObject(int seq) throws Exception {
		SimpleObject example = new SimpleObject(testString + seq, seq);
		ObjectSet result = oc.get(example);
		Assert.areEqual(1, result.size());
		Assert.areEqual(example, result.next());
	}

}

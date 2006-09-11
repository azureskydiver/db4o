/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ObjectSet;
import com.db4o.test.config.Configure;
import com.db4o.test.data.SimpleObject;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class ReadObjectQBETest extends ClientServerTestCase {

	private static String testString = "simple test string";

	protected void store() throws Exception {
		oc = openClient();
		try {
			for (int i = 0; i < Configure.CONCURRENCY_THREAD_COUNT; i++) {
				oc.set(new SimpleObject(testString + i, i));
			}
		} finally {
			oc.close();
		}
	}

	public void concReadSameObject() throws Exception {
		oc = openClient();
		try {
			int mid = Configure.CONCURRENCY_THREAD_COUNT / 2;
			SimpleObject example = new SimpleObject(testString + mid, mid);
			ObjectSet result = oc.get(example);
			Assert.areEqual(1, result.size());
			Assert.areEqual(example, result.next());
		} finally {
			oc.close();
		}
	}

	public void concReadDifferentObject(int seq) throws Exception {
		oc = openClient();
		try {
			SimpleObject example = new SimpleObject(testString + seq, seq);
			ObjectSet result = oc.get(example);
			Assert.areEqual(1, result.size());
			Assert.areEqual(example, result.next());
		} finally {
			oc.close();
		}
	}

}

/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ObjectSet;
import com.db4o.query.Query;
import com.db4o.test.config.Configure;
import com.db4o.test.data.SimpleObject;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class ReadObjectSODATest extends ClientServerTestCase {

	private String testString = "simple test string";

	protected void store() throws Exception {
		oc = openClient();
		try {
			int total = Configure.CONCURRENCY_THREAD_COUNT;
			for (int i = 0; i < total; i++) {
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
			Query query = oc.query();
			query.descend("_s").constrain(testString + mid).and(
					query.descend("_i").constrain(new Integer(mid)));
			ObjectSet result = query.execute();
			Assert.areEqual(1, result.size());
			SimpleObject expected = new SimpleObject(testString + mid, mid);
			Assert.areEqual(expected, result.next());
		} finally {
			oc.close();
		}
	}

	public void concReadDifferentObject(int seq) throws Exception {
		oc = openClient();
		try {
			Query query = oc.query();
			query.descend("_s").constrain(testString + seq).and(
					query.descend("_i").constrain(new Integer(seq)));
			ObjectSet result = query.execute();
			Assert.areEqual(1, result.size());
			SimpleObject expected = new SimpleObject(testString + seq, seq);
			Assert.areEqual(expected, result.next());
		} finally {
			oc.close();
		}
	}
}

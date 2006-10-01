/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ObjectSet;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;
import com.db4o.test.config.TestConfigure;
import com.db4o.test.persistent.SimpleObject;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class ReadObjectSODATest extends ClientServerTestCase {

	private static String testString = "simple test string";

	protected void store(ExtObjectContainer oc) throws Exception {

		for (int i = 0; i < TestConfigure.CONCURRENCY_THREAD_COUNT; i++) {
			oc.set(new SimpleObject(testString + i, i));
		}

	}

	public void concReadSameObject(ExtObjectContainer oc) throws Exception {

		int mid = TestConfigure.CONCURRENCY_THREAD_COUNT / 2;
		Query query = oc.query();
		query.descend("_s").constrain(testString + mid).and(
				query.descend("_i").constrain(new Integer(mid)));
		ObjectSet result = query.execute();
		Assert.areEqual(1, result.size());
		SimpleObject expected = new SimpleObject(testString + mid, mid);
		Assert.areEqual(expected, result.next());

	}

	public void concReadDifferentObject(ExtObjectContainer oc, int seq)
			throws Exception {

		Query query = oc.query();
		query.descend("_s").constrain(testString + seq).and(
				query.descend("_i").constrain(new Integer(seq)));
		ObjectSet result = query.execute();
		Assert.areEqual(1, result.size());
		SimpleObject expected = new SimpleObject(testString + seq, seq);
		Assert.areEqual(expected, result.next());

	}
}

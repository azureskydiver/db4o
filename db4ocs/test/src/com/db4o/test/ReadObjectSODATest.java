/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;
import com.db4o.test.config.Configure;
import com.db4o.test.data.SimpleObject;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class ReadObjectSODATest extends ClientServerTestCase {

	ObjectContainer oc;

	private String testString = "simple test string";

	protected void store() {
		oc = getObjectContainer();
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
		Query query = oc.query();
		query.descend("_s").constrain(testString + mid).and(
				query.descend("_i").constrain(new Integer(mid)));
		ObjectSet result = query.execute();
		Assert.areEqual(1, result.size());
		SimpleObject expected = new SimpleObject(testString + mid, mid);
		Assert.areEqual(expected, result.next());
	}

	public void concReadDifferentObject(int seq) throws Exception {
		Query query = oc.query();	
		query.descend("_s").constrain(testString + seq).and(
				query.descend("_i").constrain(new Integer(seq)));
		ObjectSet result = query.execute();
		Assert.areEqual(1, result.size());
		SimpleObject expected = new SimpleObject(testString + seq, seq);
		Assert.areEqual(expected, result.next());
	}

}

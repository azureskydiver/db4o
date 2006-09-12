/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ObjectSet;
import com.db4o.query.Query;
import com.db4o.test.config.Configure;
import com.db4o.test.data.SimpleObject;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class UpdateObjectTest extends ClientServerTestCase {

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

	public void concUpdateSameObject(int seq) throws Exception {
		oc = openClient();
		try {
			int mid = Configure.CONCURRENCY_THREAD_COUNT / 2;
			Query query = oc.query();
			query.descend("_s").constrain(testString + mid);
			ObjectSet result = query.execute();
			Assert.areEqual(1, result.size());
			SimpleObject o = (SimpleObject) result.next();
			o.setI(Configure.CONCURRENCY_THREAD_COUNT + seq);
			oc.set(o);
		} finally {
			oc.close();
		}
	}

	public void checkUpdateSameObject() throws Exception {
		oc = openClient();
		try {
			int mid = Configure.CONCURRENCY_THREAD_COUNT / 2;
			Query query = oc.query();
			query.descend("_s").constrain(testString + mid);
			ObjectSet result = query.execute();
			Assert.areEqual(1, result.size());
			SimpleObject o = (SimpleObject) result.next();
			int i = o.getI();
			Assert.isTrue(Configure.CONCURRENCY_THREAD_COUNT <= i
					&& i <= 2 * Configure.CONCURRENCY_THREAD_COUNT);
		} finally {
			oc.close();
		}
	}

	public void concUpdateDifferentObject(int seq) throws Exception {
		oc = openClient();
		try {
			Query query = oc.query();
			query.descend("_s").constrain(testString + seq).and(
					query.descend("_i").constrain(new Integer(seq)));
			ObjectSet result = query.execute();
			Assert.areEqual(1, result.size());
			SimpleObject o = (SimpleObject) result.next();
			o.setI(seq + 1);
			oc.set(o);
		} finally {
			oc.close();
		}
	}

	public void checkUpdateDifferentObject() throws Exception {
		oc = openClient();
		try {
			ObjectSet result = oc.query(SimpleObject.class);
			Assert.areEqual(Configure.CONCURRENCY_THREAD_COUNT, result.size());
			while (result.hasNext()) {
				SimpleObject o = (SimpleObject) result.next();
				int i = o.getI() - 1;
				Assert.areEqual(testString + i, o.getS());
			}
		} finally {
			oc.close();
		}
	}
}

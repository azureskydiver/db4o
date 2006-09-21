/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.db4o.ObjectSet;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.test.config.Configure;
import com.db4o.test.data.SimpleObject;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class UpdateCollectionTest extends ClientServerTestCase {
	private static String testString = "simple test string";

	private List list = new ArrayList();

	protected void store(ExtObjectContainer oc) throws Exception {

		for (int i = 0; i < Configure.CONCURRENCY_THREAD_COUNT; i++) {
			SimpleObject o = new SimpleObject(testString + i, i);
			list.add(o);
		}
		oc.set(list);

	}

	public void concUpdateSameElement(ExtObjectContainer oc, int seq)
			throws Exception {

		int mid = Configure.CONCURRENCY_THREAD_COUNT / 2;

		ObjectSet result = oc.get(List.class);
		Assert.areEqual(1, result.size());
		List l = (ArrayList) result.next();
		Assert.areEqual(Configure.CONCURRENCY_THREAD_COUNT, l.size());
		boolean found = false;
		Iterator iter = l.iterator();
		while (iter.hasNext()) {
			SimpleObject o = (SimpleObject) iter.next();
			// find the middle element, by comparing SimpleObject.s
			if ((testString + mid).equals(o.getS())) {
				o.setI(Configure.CONCURRENCY_THREAD_COUNT + seq);
				found = true;
				break;
			}
		}
		Assert.isTrue(found);
		oc.set(l);

	}

	public void checkUpdateSameElement(ExtObjectContainer oc) throws Exception {

		int mid = Configure.CONCURRENCY_THREAD_COUNT / 2;

		ObjectSet result = oc.get(List.class);
		Assert.areEqual(1, result.size());
		List l = (ArrayList) result.next();
		Assert.areEqual(Configure.CONCURRENCY_THREAD_COUNT, l.size());
		boolean found = false;
		Iterator iter = l.iterator();
		while (iter.hasNext()) {
			SimpleObject o = (SimpleObject) iter.next();
			// find the middle element, by comparing SimpleObject.s
			if ((testString + mid).equals(o.getS())) {
				int i = o.getI();
				Assert.isTrue(Configure.CONCURRENCY_THREAD_COUNT <= i
						&& i <= 2 * Configure.CONCURRENCY_THREAD_COUNT);
				found = true;
				break;
			}
		}
		Assert.isTrue(found);

	}

	public void concUpdateDifferentElement(ExtObjectContainer oc, int seq)
			throws Exception {

		ObjectSet result = oc.get(List.class);
		Assert.areEqual(1, result.size());
		List l = (ArrayList) result.next();
		Assert.areEqual(Configure.CONCURRENCY_THREAD_COUNT, l.size());
		boolean found = false;
		Iterator iter = l.iterator();
		while (iter.hasNext()) {
			SimpleObject o = (SimpleObject) iter.next();
			if ((testString + seq).equals(o.getS())) {
				o.setI(Configure.CONCURRENCY_THREAD_COUNT + seq);
				oc.set(o);
				found = true;
				break;
			}
		}
		Assert.isTrue(found);

	}

	public void checkUpdateDifferentElement(ExtObjectContainer oc)
			throws Exception {

		ObjectSet result = oc.get(List.class);
		Assert.areEqual(1, result.size());
		List l = (ArrayList) result.next();
		Assert.areEqual(Configure.CONCURRENCY_THREAD_COUNT, l.size());
		Iterator iter = l.iterator();
		while (iter.hasNext()) {
			SimpleObject o = (SimpleObject) iter.next();
			int i = o.getI() - Configure.CONCURRENCY_THREAD_COUNT;
			Assert.areEqual(testString + i, o.getS());
		}

	}

	public void concUpdateList(ExtObjectContainer oc, int seq) throws Exception {

		ObjectSet result = oc.get(List.class);
		Assert.areEqual(1, result.size());
		List l = (ArrayList) result.next();
		Assert.areEqual(Configure.CONCURRENCY_THREAD_COUNT, l.size());
		Iterator iter = l.iterator();
		while (iter.hasNext()) {
			SimpleObject o = (SimpleObject) iter.next();
			// set all SimpleObject.i as thread sequence.
			o.setI(seq);
		}
		oc.set(l);

	}

	public void checkUpdateList(ExtObjectContainer oc) throws Exception {

		ObjectSet result = oc.get(List.class);
		Assert.areEqual(1, result.size());
		List l = (ArrayList) result.next();
		Assert.areEqual(Configure.CONCURRENCY_THREAD_COUNT, l.size());
		Iterator iter = l.iterator();
		SimpleObject firstElement = (SimpleObject) iter.next();
		int expectedI = firstElement.getI();
		// assert all SimpleObject.i have the same value.
		while (iter.hasNext()) {
			SimpleObject o = (SimpleObject) iter.next();
			Assert.areEqual(expectedI, o.getI());
		}

	}

}

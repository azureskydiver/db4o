package com.db4o.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.db4o.ObjectSet;
import com.db4o.test.config.Configure;
import com.db4o.test.data.SimpleObject;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class ReadCollectionQBETest extends ClientServerTestCase {

	private static String testString = "simple test string";

	private List list = new ArrayList();

	protected void store() throws Exception {
		oc = openClient();
		try {
			for (int i = 0; i < Configure.CONCURRENCY_THREAD_COUNT; i++) {
				SimpleObject o = new SimpleObject(testString + i, i);
				list.add(o);
			}
			oc.set(list);
		} finally {
			oc.close();
		}
	}

	public void concReadCollection() throws Exception {
		oc = openClient();
		try {
			ObjectSet result = oc.get(new ArrayList());
			Assert.areEqual(1, result.size());
			List resultList = (List) result.next();
			Assert.areEqual(list, resultList);
		} finally {
			oc.close();
		}
	}
}

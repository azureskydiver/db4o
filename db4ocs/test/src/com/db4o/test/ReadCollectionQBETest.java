/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.ArrayList;
import java.util.List;

import com.db4o.ObjectSet;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.test.config.TestConfigure;
import com.db4o.test.persistent.SimpleObject;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class ReadCollectionQBETest extends ClientServerTestCase {

	private static String testString = "simple test string";

	private List list = new ArrayList();

	protected void store(ExtObjectContainer oc) throws Exception {
		for (int i = 0; i < TestConfigure.CONCURRENCY_THREAD_COUNT; i++) {
			SimpleObject o = new SimpleObject(testString + i, i);
			list.add(o);
		}
		oc.set(list);
	}

	public void concReadCollection(ExtObjectContainer oc) throws Exception {

		ObjectSet result = oc.get(new ArrayList());
		Assert.areEqual(1, result.size());
		List resultList = (List) result.next();
		Assert.areEqual(list, resultList);

	}
}

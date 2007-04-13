/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.test.config.*;
import com.db4o.test.persistent.*;

import db4ounit.*;

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

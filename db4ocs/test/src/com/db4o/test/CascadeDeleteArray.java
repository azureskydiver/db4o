/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.Db4o;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.test.config.TestConfigure;
import com.db4o.test.persistent.SimpleObject;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;
import db4ounit.extensions.Db4oUtil;

public class CascadeDeleteArray extends ClientServerTestCase {

	private SimpleObject[] elements;

	private int TOTAL_COUNT = TestConfigure.CONCURRENCY_THREAD_COUNT;

	public void configure() {
		Db4o.configure().objectClass(this).cascadeOnDelete(true);
	}

	public void store(ExtObjectContainer oc) {
		elements = new SimpleObject[TOTAL_COUNT];
		for (int i = 0; i < TOTAL_COUNT; ++i) {
			elements[i] = new SimpleObject("testString" + i, i);
		}

	}

	public void conc(ExtObjectContainer oc) {
		int size = Db4oUtil.occurrences(oc, SimpleObject.class);
		if (size != TOTAL_COUNT && size != 0) {
			Assert.fail("Error size " + size + "! The size should either be "
					+ TOTAL_COUNT + " or 0");
		}
		if (size == 0) {
			oc.delete(CascadeDeleteArray.class);
			Db4oUtil.assertOccurrences(oc, SimpleObject.class, 0);
			oc.rollback();
			Db4oUtil.assertOccurrences(oc, SimpleObject.class, 0);
		} else {
			oc.delete(CascadeDeleteArray.class);
			Db4oUtil.assertOccurrences(oc, SimpleObject.class, 0);
			oc.rollback();
			Db4oUtil.assertOccurrences(oc, SimpleObject.class, TOTAL_COUNT);
			oc.commit();
			oc.delete(CascadeDeleteArray.class);
			Db4oUtil.assertOccurrences(oc, SimpleObject.class, 0);
		}
	}
}

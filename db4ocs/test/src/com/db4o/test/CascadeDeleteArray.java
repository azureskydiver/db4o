/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.Db4o;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.test.config.TestConfigure;
import com.db4o.test.persistent.SimpleObject;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;
import db4ounit.extensions.Db4oUtil;

public class CascadeDeleteArray extends ClientServerTestCase {

	private SimpleObject[] elements;

	private int TOTAL_COUNT = TestConfigure.CONCURRENCY_THREAD_COUNT;

	public void configure(Configuration config) {
		super.configure(config);
		Db4o.configure().objectClass(this).cascadeOnDelete(true);
	}

	public void store(ExtObjectContainer oc) {
		elements = new SimpleObject[TOTAL_COUNT];
		for (int i = 0; i < TOTAL_COUNT; ++i) {
			elements[i] = new SimpleObject("testString" + i, i);
		}
		oc.set(this);
	}

	public void concDelete(ExtObjectContainer oc) {
		int size = Db4oUtil.occurrences(oc, SimpleObject.class);
		if (size != TOTAL_COUNT && size != 0) {
			Assert.fail("Error size " + size + "! The size should either be "
					+ TOTAL_COUNT + " or 0");
		}
		if (size == 0) {
			CascadeDeleteArray cda = (CascadeDeleteArray) Db4oUtil.getOne(oc, this);
			oc.delete(cda);
			Db4oUtil.assertOccurrences(oc, SimpleObject.class, 0);
			oc.rollback();
			Db4oUtil.assertOccurrences(oc, SimpleObject.class, 0);
		} else {
			CascadeDeleteArray cda = (CascadeDeleteArray) Db4oUtil.getOne(oc, this);
			oc.delete(cda);
			Db4oUtil.assertOccurrences(oc, SimpleObject.class, 0);
			oc.rollback();
			Db4oUtil.assertOccurrences(oc, SimpleObject.class, TOTAL_COUNT);
			oc.delete(cda);
			Db4oUtil.assertOccurrences(oc, SimpleObject.class, 0);
		}
	}
	
	public void checkDelete(ExtObjectContainer oc) {
		Db4oUtil.assertOccurrences(oc, SimpleObject.class, 0);
	}

	public void test() {
		int total = 10;
		ExtObjectContainer[] ocs = new ExtObjectContainer[total];
		ObjectSet[] oss = new ObjectSet[total];
		for (int i = 0; i < total; i++) {
			ocs[i] = fixture().db();
			oss[i] = ocs[i].query(SimpleObject.class);
			Assert.areEqual(TOTAL_COUNT, oss[i].size());
		}
		for (int i = 0; i < total; i++) {
			Db4oUtil.deleteObjectSet(ocs[i], oss[i]);
		}
		ExtObjectContainer oc = fixture().db();
		try {
			Db4oUtil.assertOccurrences(oc, SimpleObject.class, TOTAL_COUNT);
			ocs[0].commit();
			Db4oUtil.assertOccurrences(oc, SimpleObject.class, 0);
			for (int i = 1; i < total; i++) {
				ocs[i].close();
			}
			Db4oUtil.assertOccurrences(oc, SimpleObject.class, 0);
		} finally {
			oc.close();
		}
	}

}

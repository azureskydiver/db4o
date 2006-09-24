/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.regression;

import com.db4o.ext.ExtObjectContainer;
import com.db4o.test.config.Configure;
import com.db4o.test.data.SimpleObject;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class SetRollbackTest extends ClientServerTestCase {

	/*
	 * regression test: http://developer.db4o.com/forums/thread/29298.aspx
	 * 
	 */
	public void concSetRollback1(ExtObjectContainer oc, int seq) {
		if (seq != 0) {
			return;
		}
		ExtObjectContainer oc2 = db();
		try {
			for (int i = 0; i < 1000; i++) {
				SimpleObject c = new SimpleObject("oc " + i, i);
				SimpleObject c2 = new SimpleObject("oc2 " + i, i);
				oc.set(c);
				oc2.set(c2);
				oc2.rollback();
				c2 = new SimpleObject("oc2.2 " + i, i);
				oc2.set(c2);
			}
			oc.commit();
			oc2.rollback();
			Assert.areEqual(1000, oc.query(SimpleObject.class).size());
			Assert.areEqual(1000, oc2.query(SimpleObject.class).size());
		} finally {
			oc2.close();
		}
	}

	public void concSetRollback2(ExtObjectContainer oc, int seq) {
		if (seq % 2 == 0) { // if the thread sequence is even, store something
			for (int i = 0; i < 1000; i++) {
				SimpleObject c = new SimpleObject("oc " + i, i);
				oc.set(c);
			}
		} else { // if the thread sequence is odd, rollback
			for (int i = 0; i < 1000; i++) {
				SimpleObject c = new SimpleObject("oc " + i, i);
				oc.set(c);
				oc.rollback();
				c = new SimpleObject("oc2.2 " + i, i);
				oc.set(c);
			}
			oc.rollback();
		}
	}

	public void checkSetRollback2(ExtObjectContainer oc) {
		Assert.areEqual(Configure.CONCURRENCY_THREAD_COUNT / 2 * 1000, oc
				.query(SimpleObject.class).size());
	}
}

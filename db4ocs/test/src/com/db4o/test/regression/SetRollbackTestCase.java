/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.test.regression;

import com.db4o.ext.*;
import com.db4o.test.persistent.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class SetRollbackTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new SetRollbackTestCase().runConcurrency();
	}
	
	/*
	 * regression test: http://developer.db4o.com/forums/thread/29298.aspx
	 */
	public void testSetRollback() {
		ExtObjectContainer oc1 = openNewClient();
		ExtObjectContainer oc2 = openNewClient();
		try {
			for (int i = 0; i < 1000; i++) {
				SimpleObject obj1 = new SimpleObject("oc " + i, i);
				SimpleObject obj2 = new SimpleObject("oc2 " + i, i);
				oc1.set(obj1);
				oc2.set(obj2);
				oc2.rollback();
				obj2 = new SimpleObject("oc2.2 " + i, i);
				oc2.set(obj2);
			}
			oc1.commit();
			oc2.rollback();
			Assert.areEqual(1000, oc1.query(SimpleObject.class).size());
			Assert.areEqual(1000, oc2.query(SimpleObject.class).size());
		} finally {
			oc1.close();
			oc2.close();
		}
	}

	public void concSetRollback(ExtObjectContainer oc, int seq) {
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

	public void checkSetRollback(ExtObjectContainer oc) {
		Assert.areEqual(threadCount() / 2 * 1000, oc.query(SimpleObject.class)
				.size());
	}
}

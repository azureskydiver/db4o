/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.test.concurrency;

import com.db4o.ext.*;
import com.db4o.test.persistent.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class SetRollbackTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new SetRollbackTestCase().runConcurrency();
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

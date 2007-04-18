/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.test.concurrency.assorted;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runConcurrency();
	}

	protected Class[] testCases() {
		return new Class[] { 
				DeleteUpdateTestCase.class,
				RollbackDeleteTestCase.class,
				RollbackUpdateTestCase.class,
				RollbackUpdateCascadeTestCase.class,
		};
	}

}

/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.test.concurrency.regression;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	protected Class[] testCases() {
		return new Class[] { 
				SetRollbackTestCase.class, 
				Case1207TestCase.class,
				SetRollbackTestCase.class,
		};
	}

	public static void main(String[] args) {
		new AllTests().runConcurrency();
	}
}

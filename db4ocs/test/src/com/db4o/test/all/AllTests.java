/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.all;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {
	public static void main(String[] args) {
		new AllTests().runConcurrency();
	}

	protected Class[] testCases() {
		return new Class[] { 
				com.db4o.test.AllTests.class,
				com.db4o.test.mixed.AllTests.class,
				com.db4o.test.regression.AllTests.class, 
		};
	}

}

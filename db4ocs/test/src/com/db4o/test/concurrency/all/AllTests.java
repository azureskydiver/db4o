/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.test.concurrency.all;

import db4ounit.extensions.*;

public class AllTests extends Db4oConcurrenyTestSuite {
	
	/**
	 * @sharpen.ignore test suited is executed differently under .net
	 */
	public static void main(String[] args) {
		new AllTests().runConcurrency();
	}

	protected Class[] testCases() {
		return new Class[] {
				com.db4o.test.concurrency.AllTests.class,
				com.db4o.test.concurrency.assorted.AllTests.class,
				com.db4o.test.concurrency.regression.AllTests.class,
		};
	}
}


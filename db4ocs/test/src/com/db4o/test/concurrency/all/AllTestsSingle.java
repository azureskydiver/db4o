/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.test.concurrency.all;

import db4ounit.extensions.*;

public class AllTestsSingle extends Db4oTestSuite {
	
	/**
	 * @sharpen.ignore test suited is executed differently under .net
	 */
	public static void main(String[] args) {
		new AllTestsSingle().runClientServer();
	}

	protected Class[] testCases() {
		return new Class[] {
				com.db4o.test.concurrency.AllTestsSingle.class,
				com.db4o.test.concurrency.assorted.AllTestsSingle.class,
				com.db4o.test.concurrency.regression.AllTestsSingle.class,
		};
	}
}


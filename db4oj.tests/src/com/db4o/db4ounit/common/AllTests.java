/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common;

import db4ounit.extensions.Db4oTestSuite;

public class AllTests extends Db4oTestSuite {
	
	public static void main(String[] args) {
		System.exit(new AllTests().runSolo());
	}

	protected Class[] testCases() {
		return new Class[] {
			com.db4o.db4ounit.common.assorted.AllTests.class,
            com.db4o.db4ounit.common.btree.AllTests.class,
            com.db4o.db4ounit.common.fieldindex.AllTests.class,
            com.db4o.db4ounit.common.foundation.AllTests.class,
			com.db4o.db4ounit.common.handlers.AllTests.class,
			com.db4o.db4ounit.common.header.AllTests.class,
		};
	}
}

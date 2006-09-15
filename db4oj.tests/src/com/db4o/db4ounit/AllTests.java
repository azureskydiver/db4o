/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit;

import db4ounit.extensions.Db4oTestSuite;

public class AllTests extends Db4oTestSuite {
	
	public static void main(String[] args) {
		System.exit(new AllTests().runSolo());
	}

	public Class[] testCases() {
		return new Class[] {
			com.db4o.db4ounit.assorted.AllTests.class,
            com.db4o.db4ounit.btree.AllTests.class,
            com.db4o.db4ounit.events.AllTests.class,
            com.db4o.db4ounit.fieldindex.AllTests.class,
            com.db4o.db4ounit.foundation.AllTests.class,
			com.db4o.db4ounit.header.AllTests.class,
			com.db4o.db4ounit.tools.AllTests.class,
			com.db4o.db4ounit.marshall.AllTests.class,
		};
	}
}

/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre11;

import db4ounit.extensions.Db4oTestSuite;

public class AllTests extends Db4oTestSuite {
	
	public static void main(String[] args) {
		System.exit(new AllTests().runSolo());
	}

	protected Class[] testCases() {
		return new Class[] {
			com.db4o.db4ounit.common.AllTests.class,
			com.db4o.db4ounit.jre11.assorted.AllTests.class,
            com.db4o.db4ounit.jre11.btree.AllTests.class,
            com.db4o.db4ounit.jre11.constraints.AllTests.class,
            com.db4o.db4ounit.jre11.defragment.AllTests.class,
            com.db4o.db4ounit.jre11.events.AllTests.class,
            com.db4o.db4ounit.jre11.regression.AllTests.class,
            com.db4o.db4ounit.jre11.soda.AllTests.class,
            com.db4o.db4ounit.jre11.types.AllTests.class,
			com.db4o.db4ounit.jre11.tools.AllTests.class,
		};
	}
}

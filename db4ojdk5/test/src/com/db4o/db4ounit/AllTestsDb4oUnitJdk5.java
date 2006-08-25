/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit;

import db4ounit.extensions.Db4oTestSuite;

public class AllTestsDb4oUnitJdk5 extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTestsDb4oUnitJdk5().runSolo();
	}

	@Override
	protected Class[] testCases() {
		return new Class[] {
			com.db4o.db4ounit.AllTests.class,
		};
	}

}

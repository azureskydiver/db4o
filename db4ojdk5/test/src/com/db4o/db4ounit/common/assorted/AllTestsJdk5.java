/* Copyright (C) 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import db4ounit.extensions.*;

public class AllTestsJdk5 extends Db4oConcurrencyTestSuite {

	public static void main(String[] args) {
		System.exit(new AllTestsJdk5().runAll());
    }

	protected Class[] testCases() {
		return new Class[] {
			ComparatorSortTestCase.class,
			ConstructorNotRequiredTestCase.class,
		};
	}
}

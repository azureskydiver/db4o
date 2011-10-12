/* Copyright (C) 2006  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.types;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runSolo();
    }

	protected Class[] testCases() {
		return new Class[] {
				com.db4o.db4ounit.common.types.arrays.AllTests.class,
				StoreExceptionTestCase.class,
				StoreTopLevelPrimitiveTestCase.class,
				StringBuilderHandlerTestCase.class,
				UnmodifiableListTestCase.class,
		};
    }
}

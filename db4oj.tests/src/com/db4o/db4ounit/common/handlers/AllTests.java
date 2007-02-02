package com.db4o.db4ounit.common.handlers;

import db4ounit.extensions.*;


public class AllTests extends Db4oTestSuite {
	public static void main(String[] args) {
		new AllTests().runSolo();
    }

	protected Class[] testCases() {
		return new Class[] {
			StringHandlerTestCase.class,
			DoubleHandlerTestCase.class,
		};
    }

}

package com.db4o.ta.tests;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {
	
	protected Class[] testCases() {
		return new Class[] {
			TransparentActivationSupportTestCase.class,
			TransparentActivationTestCase.class,
		};
	}

}

/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.ta;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {
	
	protected Class[] testCases() {
		return new Class[] {
			TransparentActivationDiagnosticsTestCase.class,
			TransparentActivationSupportTestCase.class,
			TransparentActivationTestCase.class,
		};
	}
	
	public static void main(String[] args) {
		new AllTests().runAll();
	}

}

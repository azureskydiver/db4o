package com.db4o.ta.test;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {
	
	protected Class[] testCases() {
		return new Class[] {
			TransparentActivationsDiagnosticsTestCase.class,
			TransparentActivationSupportTestCase.class,
			TransparentActivationTestCase.class,
		};
	}
	
	public static void main(String[] args) {
		new AllTests().runSolo();
	}

}

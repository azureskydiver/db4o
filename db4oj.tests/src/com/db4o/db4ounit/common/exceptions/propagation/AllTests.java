package com.db4o.db4ounit.common.exceptions.propagation;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {
	
	public static void main(String[] args) {
		new AllTests().runAll();
	}

	protected Class[] testCases() {
		return new Class[] {
				ExceptionDuringTopLevelCallTestSuite.class,
				com.db4o.db4ounit.common.exceptions.propagation.cs.AllTests.class,
		};
	}

}

/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.ext;

import db4ounit.extensions.Db4oTestSuite;

public class AllTests extends Db4oTestSuite {
	
	public static void main(String[] args) {
		new AllTests().runSolo();
	}

	protected Class[] testCases() {
		return new Class[] {
			Db4oDatabaseTestCase.class,
			RefreshTestCase.class,
			StoredClassTestCase.class,
		};
	}

}

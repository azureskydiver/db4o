/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.regression;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	protected Class[] testCases() {
		return new Class[] { 
				SetRollbackTest.class, 
		};
	}

	public static void main(String[] args) {
		new AllTests().runConcurrency();
	}
}

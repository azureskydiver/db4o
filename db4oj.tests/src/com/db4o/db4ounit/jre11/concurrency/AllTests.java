/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre11.concurrency;

import db4ounit.extensions.*;

public class AllTests extends Db4oConcurrenyTestSuite {
	
	public static void main(String[] args) {
		new AllTests().runConcurrency();
	}

	protected Class[] testCases() {
		return new Class[] {
			com.db4o.db4ounit.common.concurrency.AllTests.class,
			HashtableModifiedUpdateDepthTestCase.class,
		};
	}

}

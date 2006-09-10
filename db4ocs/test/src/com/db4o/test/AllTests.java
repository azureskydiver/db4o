/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import db4ounit.extensions.CSTestSuite;

public class AllTests extends CSTestSuite {

	protected Class[] testCases() {
		return new Class[] {
				ReadObjectQBETest.class,
				ReadObjectSODATest.class,
				ReadObjectNQTest.class,
				};
	}
	
	public static void main(String[] args) {
		new AllTests().run();
	}
}

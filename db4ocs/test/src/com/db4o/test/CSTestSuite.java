/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
package com.db4o.test;

import db4ounit.TestSuiteBuilder;

public class CSTestSuite implements TestSuiteBuilder {

	public db4ounit.TestSuite build() {
		return new CSTestSuiteBuilder(new Class[] { ReadObjectQBETest.class,
				ReadObjectSODATest.class, }).build();
	}
}

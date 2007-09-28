/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.foundation.network;

import db4ounit.*;


public class AllTests implements TestSuiteBuilder {
	
	public TestSuite build() {
		return new ReflectionTestSuiteBuilder(new Class[] {
			NetworkSocketTestCase.class,
		}).build();	
	}
	
	public static void main(String[] args) {
		new TestRunner(AllTests.class).run();
	}

}

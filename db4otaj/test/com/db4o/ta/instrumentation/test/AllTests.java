/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.ta.instrumentation.test;

import com.db4o.ta.instrumentation.test.integration.*;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {
	
	public static void main(String[] args) {
		new AllTests().runSolo();
	}

	protected Class[] testCases() {
		return new Class[] {
			Db4oJarEnhancerTestCase.class,
			TransparentPersistenceClassLoaderTestCase.class,
			TransparentActivationInstrumentationIntegrationTestCase.class,
			TAFileEnhancerTestCase.class,
		};
	}

}

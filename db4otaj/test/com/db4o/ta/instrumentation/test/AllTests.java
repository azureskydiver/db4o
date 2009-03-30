/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.ta.instrumentation.test;

import com.db4o.ta.instrumentation.test.collections.*;
import com.db4o.ta.instrumentation.test.integration.*;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {
	
	public static void main(String[] args) {
		System.exit(new AllTests().runSolo());
	}

	protected Class[] testCases() {
		return new Class[] {
			ArrayListInstantiationInstrumentationTestCase.class,
			Db4oJarEnhancerTestCase.class,
			HashMapInstantiationInstrumentationTestCase.class,
			TransparentPersistenceClassLoaderTestCase.class,
			TransparentActivationInstrumentationIntegrationTestCase.class,
			TACollectionFileEnhancerTestSuite.class,
			TAFileEnhancerTestCase.class,
		};
	}

}

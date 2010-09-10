/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import com.db4o.drs.test.versant.jdo.reflect.JdoClassTestCase;

import db4ounit.*;

public class AllTests extends ReflectionTestSuite {
	
	public static void main(String[] args) {
		new AllTests().run();
	}

	protected Class[] testCases() {
		return new Class[] {
 			AllVodDrsIntegrationTests.class,
			com.db4o.drs.test.versant.eventlistener.AllTests.class,
			JdoMetadataGeneratorTestCase.class,
			JdoClassTestCase.class,
			VodCobraTestCase.class,
			VodDatabaseLifecycleTestCase.class,
			VodDatabaseTestCase.class,
			VodEventTestCase.class,
			VodJviTestCase.class,
			VodProviderTestCase.class,
			VodSimpleObjectContainerTestCase.class,
		};
	}

}

/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.reflect;

import db4ounit.extensions.*;


public class AllTests extends Db4oTestSuite {

	protected Class[] testCases() {
		return new Class[] {
			GenericReflectorStateTest.class,
			NoTestConstructorsTestCase.class,
			ReflectArrayTestCase.class,
			ReflectFieldExceptionTestCase.class,
			com.db4o.db4ounit.common.reflect.custom.AllTests.class,
			com.db4o.db4ounit.common.reflect.generic.GenericObjectsTest.class,
		};
	}
	
	public static void main(String[] args) {
		new AllTests().runSolo();
	}

}

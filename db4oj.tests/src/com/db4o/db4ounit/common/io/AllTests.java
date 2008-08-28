package com.db4o.db4ounit.common.io;

import com.db4o.db4ounit.common.util.*;

import db4ounit.*;


public class AllTests extends ReflectionTestSuite {

	protected Class[] testCases() {
		Class[] commonCases = {
				IoAdapterTest.class,
		};
		return Db4oUnitTestUtil.mergeClasses(commonCases, stackTraceBasedCases());
	}

	/**
	 * @decaf.replaceFirst return new Class[0];
	 */
	private Class[] stackTraceBasedCases() {
		return new Class[] {
				DiskFullTestCase.class,
				StackBasedDiskFullTestCase.class,
		};
	}

}

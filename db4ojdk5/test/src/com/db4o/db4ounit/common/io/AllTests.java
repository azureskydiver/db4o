package com.db4o.db4ounit.common.io;

import db4ounit.*;

public class AllTests extends ReflectionTestSuite {

	protected Class[] testCases() {
		return new Class[] {
				DiskFullTestCase.class,
				StackBasedDiskFullTestCase.class,
		};
	}

}

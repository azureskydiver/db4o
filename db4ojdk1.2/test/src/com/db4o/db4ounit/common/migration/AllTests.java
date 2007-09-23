package com.db4o.db4ounit.common.migration;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {
	protected Class[] testCases() {
		return new Class[]{
			Db4oMigrationTestSuite.class,
		};
	}
}
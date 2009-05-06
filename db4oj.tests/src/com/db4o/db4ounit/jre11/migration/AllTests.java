/* Copyright (C) 2004 - 2006 Versant Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre11.migration;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	protected Class[] testCases() {
		return new Class[] {
			// Apparently db4o 5.2 didn't support null Byte references
			//ByteMigrationTestCase.class,
			DateMigrationTestCase.class,
			DoubleMigrationTestCase.class,
			FloatMigrationTestCase.class,
			IntegerMigrationTestCase.class,
			LongMigrationTestCase.class,
			ShortMigrationTestCase.class,
		};
	}
	public static void main(String[] args) {
		new AllTests().runAll();
	}

}

package com.db4o.db4ounit.jre12;

import db4ounit.extensions.*;

public class AllTestsJdk1_2 extends Db4oTestSuite {

	public static void main(String[] args) {
		System.exit(new AllTestsJdk1_2().runSolo());
    }

	protected Class[] testCases() {
		return new Class[] {
			// FIXME: solve the workspacePath issue and uncomment migration.AllCommonTests.class below
//			com.db4o.db4ounit.common.migration.AllCommonTests.class,
			com.db4o.db4ounit.common.defragment.LegacyDatabaseDefragTestCase.class,
			com.db4o.db4ounit.common.ta.config.AllTests.class,
			com.db4o.db4ounit.common.ta.diagnostics.AllTests.class,
			com.db4o.db4ounit.common.ta.hierarchy.AllTests.class,
			com.db4o.db4ounit.common.ta.nested.AllTests.class,
			com.db4o.db4ounit.jre11.AllTests.class,
			com.db4o.db4ounit.jre12.assorted.AllTests.class,
			com.db4o.db4ounit.jre12.blobs.AllTests.class,
			com.db4o.db4ounit.jre12.defragment.AllTests.class,
			com.db4o.db4ounit.jre12.fieldindex.AllTests.class,
			com.db4o.db4ounit.jre12.soda.AllTests.class,
			com.db4o.db4ounit.jre12.collections.AllTests.class,
			com.db4o.db4ounit.jre12.collections.facades.AllTests.class,
			com.db4o.db4ounit.jre12.collections.map.AllTests.class,
			com.db4o.db4ounit.jre12.querying.AllTests.class,
			com.db4o.db4ounit.jre12.regression.AllTests.class,
		};
	}
}

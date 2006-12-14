package com.db4o.db4ounit.jre12;

import com.db4o.db4ounit.jre12.collections.map.*;

import db4ounit.extensions.*;

public class AllTestsJdk1_2 extends Db4oTestSuite {

	public static void main(String[] args) {
		System.exit(new AllTestsJdk1_2().runSolo());
    }

	protected Class[] testCases() {
		return new Class[] {
			com.db4o.db4ounit.jre11.AllTests.class,
			SimpleMapTestCase.class,
			com.db4o.db4ounit.jre12.assorted.AllTests.class,
			com.db4o.db4ounit.jre12.defragment.AllTests.class,
			com.db4o.db4ounit.jre12.fieldindex.AllTests.class,
			com.db4o.db4ounit.jre12.soda.AllTests.class,
			com.db4o.db4ounit.jre12.collections.AllTests.class,
		};
	}
}

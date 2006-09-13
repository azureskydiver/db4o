package com.db4o.db4ounit;

import com.db4o.db4ounit.collections.*;
import com.db4o.db4ounit.collections.map.*;

import db4ounit.extensions.*;

public class AllTestsJdk1_2 extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTestsJdk1_2().runSolo();
    }

	protected Class[] testCases() {
		return new Class[] {
			CollectionUuidTest.class,
			SetCollectionOnUpdateTestCase.class,
			SimpleMapTestCase.class,
			com.db4o.db4ounit.fieldindex.AllTests.class,
		};
	}
}

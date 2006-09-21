package com.db4o.db4ounit.jre12;

import com.db4o.db4ounit.jre12.collections.*;
import com.db4o.db4ounit.jre12.collections.map.SimpleMapTestCase;

import db4ounit.extensions.Db4oTestSuite;

public class AllTestsJdk1_2 extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTestsJdk1_2().runSolo();
    }

	protected Class[] testCases() {
		return new Class[] {
			com.db4o.db4ounit.jre11.AllTests.class,
			CollectionUuidTest.class,
			SetCollectionOnUpdateTestCase.class,
			SimpleMapTestCase.class,
			com.db4o.db4ounit.jre12.fieldindex.AllTests.class,
		};
	}
}

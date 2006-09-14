package com.db4o.db4ounit.jdk12;

import com.db4o.db4ounit.jdk12.collections.*;
import com.db4o.db4ounit.jdk12.collections.map.SimpleMapTestCase;

import db4ounit.extensions.Db4oTestSuite;

public class AllTestsJdk1_2 extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTestsJdk1_2().runSolo();
    }

	protected Class[] testCases() {
		return new Class[] {
			CollectionUuidTest.class,
			SetCollectionOnUpdateTestCase.class,
			SimpleMapTestCase.class,
			com.db4o.db4ounit.jdk12.fieldindex.AllTests.class,
		};
	}
}

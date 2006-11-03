/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {
	protected Class[] testCases() {
		return new Class[] {
			CollectionUuidTest.class,
			SetCollectionOnUpdateTestCase.class,
			com.db4o.db4ounit.jre12.collections.custom.AllTests.class,
			com.db4o.db4ounit.jre12.collections.map.AllTests.class,
		};
	}

}

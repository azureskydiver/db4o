/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import db4ounit.extensions.CSTestSuite;

public class AllTests extends CSTestSuite {

	protected Class[] testCases() {
		return new Class[] {
				ArrayNOrder.class,
				ByteArray.class,
				CascadedDeleteUpdate.class,
				CascadeDeleteArray.class,
				CascadeDeleteDeleted.class,
				ReadObjectQBETest.class,
				ReadObjectSODATest.class,
				ReadObjectNQTest.class,
				ReadCollectionQBETest.class,
				ReadCollectionSODATest.class,
				ReadCollectionNQTest.class,
				UpdateObjectTest.class,
				UpdateCollectionTest.class,
				};
	}
	
	public static void main(String[] args) {
		new AllTests().run();
	}
}

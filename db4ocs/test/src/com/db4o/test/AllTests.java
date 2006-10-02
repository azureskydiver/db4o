/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import db4ounit.extensions.CSTestSuite;
import db4ounit.extensions.Timer;

public class AllTests extends CSTestSuite {

	protected Class[] testCases() {
		return new Class[] {
				ArrayNOrder.class,
				ByteArray.class,
				CascadeDeleteArray.class,
				CascadeDeleteDeleted.class,
				CascadeDeleteFalse.class,
				CascadeOnActivate.class,
				CascadeOnSet.class,
				CascadeOnUpdate.class,
				CascadeOnUpdate2.class,
				CascadeToExistingVectorMember.class,
				CascadeToHashtable.class,
				CascadeToVector.class,
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
		Timer timer = new Timer();
		timer.start();
		new AllTests().run();
		timer.stop();
		System.out.println("Time elapsed: "+ timer.elapsed()+"ms");
	}
}

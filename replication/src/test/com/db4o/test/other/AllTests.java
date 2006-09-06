package com.db4o.test.other;

import com.db4o.test.replication.db4ounit.DrsTestSuite;

public class AllTests extends DrsTestSuite {
	
	protected Class[] testCases() {
		return new Class[] {
			CollectionUuidTest.class,
			GetByUUID.class
		};
	}
	
	public static void main(String[] args) {
		 new AllTests().runDb4oDb4o();
	}

}

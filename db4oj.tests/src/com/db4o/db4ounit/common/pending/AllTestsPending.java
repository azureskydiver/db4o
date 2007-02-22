package com.db4o.db4ounit.common.pending;

import db4ounit.extensions.*;

/**
 * Failing test cases
 */
public class AllTestsPending extends Db4oTestSuite {

	protected Class[] testCases() {
		return new Class[] {
				SortMultipleTestCase.class,
		};
	}
	
	public static void main(String[] args) {
		new AllTestsPending().runSolo();
	}

}

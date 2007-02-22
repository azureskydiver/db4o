package com.db4o.db4ounit.jre12.pending;

import db4ounit.extensions.*;

/**
 * Failing test cases
 */
public class AllTestsPending extends Db4oTestSuite {

	protected Class[] testCases() {
		return new Class[] {
				com.db4o.db4ounit.common.pending.AllTestsPending.class,
				MapNullValueTestCase.class,
		};
	}
	
	public static void main(String[] args) {
		new AllTestsPending().runSolo();
	}

}

package com.db4o.ibs.tests;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {
	
	@Override
	protected Class[] testCases() {
		return new Class[] {
			ChangeSetPublisherTestCase.class,
			SlotBasedChangeSetBuilderTestCase.class,
			SlotBasedChangeSetProcessorTestCase.class,
		};
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new AllTests().runSolo();
	}
}

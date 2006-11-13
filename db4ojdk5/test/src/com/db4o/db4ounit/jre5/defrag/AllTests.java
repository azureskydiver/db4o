package com.db4o.db4ounit.jre5.defrag;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	protected Class[] testCases() {
		return new Class[] { RunTestsDefrag.class };
	}

	public static void main(String[] args) {
		System.exit(new AllTests().runSolo());
	}
}

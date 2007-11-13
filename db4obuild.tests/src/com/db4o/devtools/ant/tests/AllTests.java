package com.db4o.devtools.ant.tests;

import db4ounit.extensions.Db4oTestSuite;

public class AllTests extends Db4oTestSuite {
	
	@Override
	protected Class[] testCases() {
		return new Class[] {
			FolderDiffTestCase.class
		};
	}
	
	public static void main(String[] args) {
		new AllTests().runAll();
	}

}

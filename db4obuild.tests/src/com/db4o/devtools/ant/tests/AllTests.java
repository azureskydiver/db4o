package com.db4o.devtools.ant.tests;

import db4ounit.*;

public class AllTests extends ReflectionTestSuite {
	
	@Override
	protected Class[] testCases() {
		return new Class[] {
			FolderDiffTestCase.class
		};
	}
	
	public static void main(String[] args) {
		new AllTests().run();
	}
}

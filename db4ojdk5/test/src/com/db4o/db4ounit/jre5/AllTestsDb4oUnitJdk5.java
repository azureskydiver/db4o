/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre5;

import db4ounit.extensions.*;

public class AllTestsDb4oUnitJdk5 extends Db4oTestSuite {

	public static void main(String[] args) {
		System.exit(new AllTestsDb4oUnitJdk5().runAll());
	}

	@Override
	protected Class[] testCases() {
		return new Class[] {
			com.db4o.db4ounit.jre5.enums.AllTests.class,
			com.db4o.db4ounit.jre12.AllTestsJdk1_2.class,
			com.db4o.db4ounit.jre5.annotation.IndexedAnnotationTestCase.class,
		};
	}

}

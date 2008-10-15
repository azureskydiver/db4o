/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.reflect.custom;

import db4ounit.extensions.*;


public class AllTests extends Db4oTestSuite {

	protected Class[] testCases() {
		return new Class[] {
			CustomReflectorTestCase.class,
		};
	}

	public static void main(String[] args) {
		new AllTests().runSolo();
	}

}

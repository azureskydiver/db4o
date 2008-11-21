/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre5.generic;


import db4ounit.extensions.*;



/**
 * @decaf.ignore
 */
public class AllTests extends Db4oTestSuite {
	
	public static void main(String[] args) {
		new AllTests().runAll();
	}
	
	@Override
	protected Class[] testCases() {
		return new Class[] {
			GenericStringTestCase.class,
		};
	}

}
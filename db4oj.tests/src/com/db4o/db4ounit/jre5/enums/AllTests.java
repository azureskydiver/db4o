/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre5.enums;


import db4ounit.extensions.*;



/**
 */
@decaf.Ignore
public class AllTests extends Db4oTestSuite {
	
	public static void main(String[] args) {
		new AllTests().runSoloAndClientServer();
	}
	
	@Override
	protected Class[] testCases() {
		return new Class[] {
			DeleteEnumTestCase.class,
			EnumTestCase.class,
			SimpleEnumTestCase.class,
			TAEnumsTestCase.class,
		};
	}

}
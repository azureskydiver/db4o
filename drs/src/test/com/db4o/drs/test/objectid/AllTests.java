/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.objectid;


import db4ounit.*;

public class AllTests extends ReflectionTestSuite {
	
	public static void main(String[] args) {
		new AllTests().run();
	}

	@Override
	protected Class[] testCases() {
		return new Class[]{
			VodObjectIdConversionTestCase.class,	
		};
	}
	

}

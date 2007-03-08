/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre11.staging;

import db4ounit.extensions.Db4oTestSuite;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runSolo();
    }

	protected Class[] testCases() {
		return new Class[] {
			com.db4o.db4ounit.common.staging.AllTests.class,
			
			/**
			 *  When you add a test here, make sure you create a Jira issue. 
			 */
			
		};
	}
}

/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.staging;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runSolo();
    }

	protected Class[] testCases() {
		return new Class[] {
			
			/**
			 *  When you add a test here, make sure you create a Jira issue. 
			 */
		    ActivateDepthTestCase.class,
		    ClientServerPingTestCase.class,
		    // TODO is there a Jira for this one?
		    InterfaceQueryTestCase.class,
			LazyQueryDeleteTestCase.class,
            PingTestCase.class,
			SODAClassTypeDescend.class,			
		};
	}
}

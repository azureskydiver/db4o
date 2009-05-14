/* Copyright (C) 2004 - 2006 Versant Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.staging;

import db4ounit.extensions.*;

public class AllTests extends ComposibleTestSuite {

	public static void main(String[] args) {
		new AllTests().runSolo();
    }

	protected Class[] testCases() {
		return composeTests(
					new Class[] {			
							/**
							 *  When you add a test here, make sure you create a Jira issue. 
							 */
							ActivateDepthTestCase.class,
							InterfaceQueryTestCase.class, // COR-1131
							LazyQueryDeleteTestCase.class,
							SODAClassTypeDescend.class,
							StoredClassUnknownClassQueryTestCase.class, // COR-1542
							UnavailableEnumTestCase.class
					});
	}
	
	/**
	 * @sharpen.if !SILVERLIGHT
	 */
	@Override
	protected Class[] composeWith() {
		return new Class[] {
						ClientServerPingTestCase.class,
						PingTestCase.class,
					};
	}
}

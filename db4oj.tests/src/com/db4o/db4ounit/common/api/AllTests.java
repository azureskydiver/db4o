package com.db4o.db4ounit.common.api;

import db4ounit.extensions.*;

public class AllTests extends ComposibleTestSuite {
	
	public static void main(String[] args) {
		new AllTests().runSolo();
	}

	@Override
	protected Class[] testCases() {
		return composeTests(
				new Class[] {
						Db4oClientServerTestCase.class,
						Db4oEmbeddedTestCase.class,
				});
	}

	/**
	 * @sharpen.if !SILVERLIGHT
	 */
	protected Class[] composeWith() {
		return new Class[] {
				CommonAndLocalConfigurationTestSuite.class,
				StoreAllTestCase.class,
		};
	}	
}

package com.db4o.db4ounit.common.config;

import db4ounit.extensions.Db4oTestSuite;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runSolo();
    }

	protected Class[] testCases() {
        
        return new Class[] {
        		NonStaticConfigurationTestCase.class,
		};
    }
}

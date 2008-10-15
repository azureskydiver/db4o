/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.config;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runSolo();
    }

	protected Class[] testCases() {
        return new Class[] {
        		ConfigurationItemTestCase.class,
        		CustomStringEncodingTestCase.class,
        		GlobalVsNonStaticConfigurationTestCase.class,
        		LatinStringEncodingTestCase.class,
        		ObjectTranslatorTestCase.class,
        		UnicodeStringEncodingTestCase.class,
        		UTF8StringEncodingTestCase.class,
        		VersionNumbersTestCase.class,
		};
    }
}

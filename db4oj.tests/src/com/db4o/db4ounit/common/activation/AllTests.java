/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.activation;

import db4ounit.extensions.Db4oTestSuite;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runSolo();
    }

	protected Class[] testCases() {
		return new Class[] {
			ActivationDepthProviderConfigTestCase.class,
			MaximumActivationDepthTestCase.class,
			TransparentActivationDepthTestCase.class,
		};
	}
}

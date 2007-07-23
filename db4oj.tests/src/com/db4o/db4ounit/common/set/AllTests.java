/* Copyright (C) 2004 - 20067 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.set;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runAll();
	}

	protected Class[] testCases() {
		return new Class[] { 
				DeepSetClientServerTestCase.class,
				DeepSetTestCase.class, 
		};
	}
}

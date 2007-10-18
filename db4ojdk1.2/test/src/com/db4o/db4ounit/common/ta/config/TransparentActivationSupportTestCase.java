/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.ta.config;

import com.db4o.db4ounit.common.ta.*;

import db4ounit.*;

public class TransparentActivationSupportTestCase extends TransparentActivationTestCaseBase {

	public static void main(String[] args) {
		new TransparentActivationSupportTestCase().runAll();
	}
	
	public void testActivationDepth() {
		Assert.areEqual(0, db().configure().activationDepth());
	}
}

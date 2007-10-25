/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.ta.config;

import com.db4o.db4ounit.common.ta.*;
import com.db4o.internal.activation.*;

import db4ounit.*;

public class TransparentActivationSupportTestCase
	extends TransparentActivationTestCaseBase {

	public static void main(String[] args) {
		new TransparentActivationSupportTestCase().runAll();
	}
	
	public void testActivationDepth() {
		Assert.isInstanceOf(TransparentActivationDepthProvider.class, stream().configImpl().activationDepthProvider());
	}
}

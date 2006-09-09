/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import db4ounit.TestRunner;

public class AllTests {
	public static void main(String[] args) {
		new TestRunner(CSTestSuite.class).run();
	}
}

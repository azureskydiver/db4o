/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.all;

import db4ounit.extensions.Timer;

public class AllTests {
	public static void main(String[] args) {
		Timer timer = new Timer();
		timer.start();
		new com.db4o.test.AllTests().run();
		new com.db4o.test.mixed.AllTests().run();
		new com.db4o.test.regression.AllTests().run();
		timer.stop();
		System.out.println("Time elapsed: " + timer.elapsed() + "ms");
	}
}

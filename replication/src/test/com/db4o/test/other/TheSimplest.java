/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.test.other;

import com.db4o.test.replication.db4ounit.DrsTestCase;


public class TheSimplest extends DrsTestCase {

	public void test() {
		storeInA();
		replicate();
		checkInB();
	}

	private void storeInA() {
	}
	
	private void replicate() {
	}
	
	private void checkInB() {
	}
}

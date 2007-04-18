/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.test.concurrency;

import com.db4o.ext.*;

import db4ounit.extensions.*;

/**
 * 
 */
public class Circular1TestCase extends AbstractDb4oTestCase {
	public static void main(String[] args) {
		new Circular1TestCase().runConcurrency();
	}

	public void store() {
		store(new C1C());
	}

	public void conc(ExtObjectContainer oc) {
		assertOccurences(oc, C1C.class, 1);
	}

	public static class C1P {
		C1C c;
	}

	public static class C1C extends C1P {
	}
}

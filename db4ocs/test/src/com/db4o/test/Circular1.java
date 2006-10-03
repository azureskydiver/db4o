/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ext.ExtObjectContainer;

import db4ounit.extensions.ClientServerTestCase;
import db4ounit.extensions.Db4oUtil;

/**
 * 
 */
public class Circular1 extends ClientServerTestCase {

	public void store(ExtObjectContainer oc) {
		oc.set(new C1C());
	}

	public void conc(ExtObjectContainer oc) {
		Db4oUtil.assertOccurrences(oc, C1C.class, 1);
	}

	public static class C1P {
		C1C c;
	}

	public static class C1C extends C1P {
	}
}

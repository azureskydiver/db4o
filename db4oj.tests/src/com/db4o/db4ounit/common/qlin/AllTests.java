/* Copyright (C) 2004 - 2006 Versant Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.qlin;

import db4ounit.extensions.*;

/**
 * not sure about Silverlight yet, let's not risk the build.
 * @sharpen.if !SILVERLIGHT
 */
@decaf.Remove(decaf.Platform.JDK11)
public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runAll(); //runSoloAndClientServer();
    }

	protected Class[] testCases() {
		return new Class[] {
			BasicQLinTestCase.class,
			PrototypesTestCase.class,
		};
	}
}

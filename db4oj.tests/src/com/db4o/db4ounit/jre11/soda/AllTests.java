/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre11.soda;

import com.db4o.db4ounit.jre11.soda.wrapper.typed.*;

import db4ounit.extensions.Db4oTestSuite;


public class AllTests extends Db4oTestSuite {

	protected Class[] testCases() {
		return new Class[] {
			STBooleanWTTestCase.class,
			STByteWTTestCase.class,
			STCharWTTestCase.class,
			STDoubleWTTestCase.class,
			STFloatWTTestCase.class,
			STIntegerWTTestCase.class,
			STLongWTTestCase.class,
			STShortWTTestCase.class,
		};
	}

}

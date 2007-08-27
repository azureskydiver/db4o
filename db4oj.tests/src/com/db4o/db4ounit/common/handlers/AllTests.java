/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.handlers;

import db4ounit.extensions.*;


public class AllTests extends Db4oTestSuite {
	public static void main(String[] args) {
		new AllTests().runSolo();
    }

	protected Class[] testCases() {
		return new Class[] {
		    ArrayHandlerTestCase.class,
            BooleanHandlerTestCase.class,
            ByteHandlerTestCase.class,
            CharHandlerTestCase.class,
		    ClassHandlerTestCase.class,
		    CustomClassHandlerTestCase.class,
            DateHandlerTestCase.class,
            DoubleHandlerTestCase.class,
            FloatHandlerTestCase.class,
            IntHandlerTestCase.class,
            LongHandlerTestCase.class,
			StringHandlerTestCase.class,
			ShortHandlerTestCase.class,
		};
    }

}

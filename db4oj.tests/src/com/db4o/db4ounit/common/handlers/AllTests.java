/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.handlers;

import db4ounit.extensions.*;


public class AllTests extends Db4oTestSuite {
	public static void main(String[] args) {
		new AllTests().runAll();
    }

	protected Class[] testCases() {
		return new Class[] {
		    ArrayHandlerTestCase.class,
            BooleanHandlerTestCase.class,
            ByteHandlerTestCase.class,
            CharHandlerTestCase.class,
		    ClassHandlerTestCase.class,
		    CustomTypeHandlerTestCase.class,
            DoubleHandlerTestCase.class,
            FloatHandlerTestCase.class,
            IntHandlerTestCase.class,
            LongHandlerTestCase.class,
            MultiDimensionalArrayHandlerTestCase.class,
            MultidimensionalArrayIterator4TestCase.class,
            SecondClassTestCase.class,
            StringBufferHandlerTestCase.class,
			StringHandlerTestCase.class,
			ShortHandlerTestCase.class,
			UntypedHandlerTestCase.class,
		};
    }

}

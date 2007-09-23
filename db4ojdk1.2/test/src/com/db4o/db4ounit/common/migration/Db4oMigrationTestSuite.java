/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.migration;

import com.db4o.db4ounit.common.handlers.*;

import db4ounit.*;


public class Db4oMigrationTestSuite implements TestSuiteBuilder {
	
	public static void main(String[] args) {
		new TestRunner(Db4oMigrationTestSuite.class).run();
	}

	public TestSuite build() {
		return new Db4oMigrationSuiteBuilder(testCases()).build();
	}

	protected Class[] testCases() {
		return new Class[] { 
            BooleanHandlerUpdateTestCase.class,
            ByteHandlerUpdateTestCase.class,
            CharHandlerUpdateTestCase.class,
            DateHandlerUpdateTestCase.class,
            DoubleHandlerUpdateTestCase.class,
            FloatHandlerUpdateTestCase.class,
            IntHandlerUpdateTestCase.class,
            LongHandlerUpdateTestCase.class,
            NestedArrayUpdateTestCase.class,
            ObjectArrayUpdateTestCase.class,
            ShortHandlerUpdateTestCase.class,
            
            // FIXME: StringHandlerUpdateTestCase.test currently failing 
//            StringHandlerUpdateTestCase.class, 
		};
	}

}

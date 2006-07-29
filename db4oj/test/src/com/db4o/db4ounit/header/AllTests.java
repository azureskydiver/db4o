/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.header;

import db4ounit.*;
import db4ounit.db4o.*;
import db4ounit.db4o.fixtures.*;

public class AllTests extends Db4oTestCase implements TestSuiteBuilder {
    
    public static void main(String[] args) {
        new TestRunner(
                new Db4oTestSuiteBuilder(
                        new Db4oSolo(),
                        AllTests.class)).run();
    }

    public TestSuite build() {
        return new Db4oTestSuiteBuilder(
                fixture(),
                new Class[] {
                    OldHeaderTest.class,
                    }).build();
    }
}

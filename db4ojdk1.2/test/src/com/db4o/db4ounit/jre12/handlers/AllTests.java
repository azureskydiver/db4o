/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.jre12.handlers;

import com.db4o.db4ounit.common.handlers.*;
import com.db4o.ext.*;

import db4ounit.extensions.*;


public class AllTests extends Db4oTestSuite {
    public static void main(String[] args) {
        new AllTests().runSolo();
    }

    protected Class[] testCases() {
        return new Class[] {
            com.db4o.db4ounit.common.handlers.AllTests.class,
            IntHandlerUpdateTestCase.class,
        };
    }

    public int runSolo() {
        Class[] classes = testCases();
        for (int i = 0; i < classes.length; i++) {
            if (FormatMigrationTestCaseBase.class.isAssignableFrom(classes[i])) {
                try {
                    Db4oVersionRunner.runDatabaseCreator(classes[i].getName());
                } catch (Exception e) {
                    throw new Db4oException(e);
                }
            }
        }
        return super.runSolo();
    }

    
}

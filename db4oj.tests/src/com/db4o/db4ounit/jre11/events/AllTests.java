/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre11.events;

import db4ounit.extensions.Db4oTestSuite;

public class AllTests extends Db4oTestSuite {

    public static void main(String[] args) {
        new AllTests().runSolo();
    }

    protected Class[] testCases() {
        return new Class[] {
        	CommitTimeCallbacksTestCase.class,
            EventRegistryTestCase.class,
            GlobalLifecycleEventsTestCase.class,
            SelectiveCascadingDeleteTestCase.class,
            SelectiveActivationTestCase.class
        };
    }
}

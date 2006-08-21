package com.db4o.objectmanager.api;

import db4ounit.TestSuiteBuilder;
import db4ounit.TestSuite;
import db4ounit.TestRunner;

/**
 * User: treeder
 * Date: Aug 9, 2006
 * Time: 10:58:30 AM
 */
public class DatabaseInspectorTestSuite implements TestSuiteBuilder {

    public static void main(String[] args) {
        new TestRunner(DatabaseInspectorTestSuite.class).run();
    }

    public TestSuite build() {
        return new ObjectManagerTestSuiteBuilder(
               new Class[]{
                        DatabaseInspectorTest.class
                }).build();
    }
}

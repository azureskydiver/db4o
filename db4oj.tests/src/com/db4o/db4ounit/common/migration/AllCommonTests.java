package com.db4o.db4ounit.common.migration;

import db4ounit.extensions.*;

/**
 * @decaf.ignore.jdk11
 */
public class AllCommonTests extends Db4oTestSuite {
    
    public static void main(String[] args) {
        new AllCommonTests().runSolo();
    }

    protected Class[] testCases() {
		return new Class[]{
			Db4oMigrationTestSuite.class,
			FieldsToTypeHandlerMigrationTestCase.class,
			TranslatorToTypehandlerMigrationTestCase.class,
		};
	}
}
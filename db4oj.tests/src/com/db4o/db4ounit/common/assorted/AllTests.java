/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;


import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runSolo();
		//new AllTests().runClientServer();
    }

	protected Class[] testCases() {
		return new Class[] {
			AliasesTestCase.class,
            BackupStressTestCase.class,
            CanUpdateFalseRefreshTestCase.class,
            CascadedDeleteReaddTestCase.class,
            ChangeIdentity.class,
            ClassMetadataTestCase.class,
            CloseUnlocksFileTestCase.class,
            ComparatorSortTestCase.class,
            DatabaseUnicityTest.class,
            DescendToNullFieldTestCase.class,
            GetByUUIDTestCase.class,
            GetSingleSimpleArrayTestCase.class,
            HandlerRegistryTestCase.class,
            IndexCreateDropTestCase.class,
            LongLinkedListTestCase.class,
            MultiDeleteTestCase.class,
            NakedObjectTestCase.class,
            ObjectMarshallerTestCase.class,
            ObjectVersionTest.class,
            PersistStaticFieldValuesTestCase.class,
            PersistTypeTestCase.class,
            PreventMultipleOpenTestCase.class,
            ReAddCascadedDeleteTestCase.class,
            SimplestPossibleTestCase.class,
            ServerRevokeAccessTestCase.class,
            SystemInfoTestCase.class,
		};
	}
}

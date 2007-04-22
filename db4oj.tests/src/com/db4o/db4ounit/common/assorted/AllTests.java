/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;


import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runSolo();
		// new AllTests().runClientServer();
    }

	protected Class[] testCases() {
		return new Class[] {
			AliasesTestCase.class,
            BackupStressTestCase.class,
            CanUpdateFalseRefreshTestCase.class,
            CascadedDeleteReadTestCase.class,
            ChangeIdentity.class,
            ClassMetadataTestCase.class,
            ClassRenameTestCase.class,
            CloseUnlocksFileTestCase.class,
            ComparatorSortTestCase.class,
            DatabaseUnicityTest.class,
            DescendToNullFieldTestCase.class,
            FileSizeOnRollbackTestCase.class,
            GetByUUIDTestCase.class,
            GetSingleSimpleArrayTestCase.class,
            HandlerRegistryTestCase.class,
            IndexCreateDropTestCase.class,
            LazyObjectReferenceTestCase.class,
            LongLinkedListTestCase.class,
            MaximumActivationDepthTestCase.class,
            MultiDeleteTestCase.class,
            NakedObjectTestCase.class,
            ObjectMarshallerTestCase.class,
            PersistStaticFieldValuesTestCase.class,
            PersistTypeTestCase.class,
            PreventMultipleOpenTestCase.class,
            ReAddCascadedDeleteTestCase.class,
            ReferenceSystemTestCase.class,
            RollbackTestCase.class,
            SimplestPossibleTestCase.class,
            SystemInfoTestCase.class,
		};
	}
}

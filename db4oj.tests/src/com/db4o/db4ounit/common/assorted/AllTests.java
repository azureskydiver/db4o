/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import db4ounit.extensions.Db4oTestSuite;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runSolo();
    }

	protected Class[] testCases() {
		return new Class[] {
			AliasesTestCase.class,
            BackupStressTestCase.class,
            CanUpdateFalseRefreshTestCase.class,
            CascadeDeleteDeletedTestCase.class,
            CascadedDeleteReadTestCase.class,
            ChangeIdentity.class,
            ClassMetadataTestCase.class,
            CloseUnlocksFileTestCase.class,
            ComparatorSortTestCase.class,
            DatabaseUnicityTest.class,
            DeleteUpdateTestCase.class,
            DescendToNullFieldTestCase.class,
            DualDeleteTestCase.class,
            GetByUUIDTestCase.class,
            GetSingleSimpleArrayTestCase.class,
            HandlerRegistryTestCase.class,
            IndexCreateDropTestCase.class,
            IndexedBlockSizeQueryTestCase.class,
            LazyObjectReferenceTestCase.class,
            LockedTreeTestCase.class,
            LongLinkedListTestCase.class,
            MaximumActivationDepthTestCase.class,
            MultiDeleteTestCase.class,
            NakedObjectTestCase.class,
            ObjectNotStorableExceptionTestCase.class,
            PersistentIntegerArrayTestCase.class,
            PersistStaticFieldValuesTestCase.class,
            PersistTypeTestCase.class,
            PreventMultipleOpenTestCase.class,
            ReAddCascadedDeleteTestCase.class,
            RollbackDeleteTestCase.class,
            RollbackTestCase.class,
			RollbackUpdateTestCase.class,
			RollbackUpdateCascadeTestCase.class,
            SimplestPossibleTestCase.class,
            SystemInfoTestCase.class,
            UpdateDepthTestCase.class,
		};
	}
}

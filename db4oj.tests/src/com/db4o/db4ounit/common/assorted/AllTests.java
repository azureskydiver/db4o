/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runSolo();
    }

	protected Class[] testCases() {
		return new Class[] {
			AliasesTestCase.class,
            BackupStressTestCase.class,
            CallbackTestCase.class,
            CanUpdateFalseRefreshTestCase.class,
            CascadeDeleteDeletedTestCase.class,
            CascadedDeleteReadTestCase.class,
            ChangeIdentity.class,
            ClassMetadataTestCase.class,
            CloseUnlocksFileTestCase.class,
            ComparatorSortTestCase.class,
            DatabaseGrowthSizeTestCase.class,
            DatabaseUnicityTest.class,
            // FIXME: COR-1060
//            DeleteSetTestCase.class,
            DeleteUpdateTestCase.class,
            DescendToNullFieldTestCase.class,
            DualDeleteTestCase.class,
            ExceptionsOnNotStorableFalseTestCase.class,
            ExceptionsOnNotStorableIsDefaultTestCase.class,
            GetSingleSimpleArrayTestCase.class,
            HandlerRegistryTestCase.class,
            IndexCreateDropTestCase.class,
            IndexedBlockSizeQueryTestCase.class,
            InMemoryObjectContainerTestCase.class,
            KnownClassesTestCase.class,
            LazyObjectReferenceTestCase.class,
            LockedTreeTestCase.class,
            LongLinkedListTestCase.class,
            MultiDeleteTestCase.class,
            ObjectConstructorTestCase.class,
            PlainObjectTestCase.class,
            PeekPersistedTestCase.class,
            PersistentIntegerArrayTestCase.class,
            PersistStaticFieldValuesTestCase.class,
            PersistTypeTestCase.class,
            PreventMultipleOpenTestCase.class,
            QueryByInterface.class,
            ReAddCascadedDeleteTestCase.class,
            RepeatDeleteReaddTestCase.class,
            RollbackDeleteTestCase.class,
            RollbackTestCase.class,
			RollbackUpdateTestCase.class,
			RollbackUpdateCascadeTestCase.class,
			SimplestPossibleNullMemberTestCase.class,
            SimplestPossibleTestCase.class,
            SimplestPossibleParentChildTestCase.class,
            SystemInfoTestCase.class,
            UpdateDepthTestCase.class,
		};
	}
}

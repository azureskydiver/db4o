/* Copyright (C) 2004 - 2006 Versant Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import db4ounit.extensions.*;

public class AllTests extends ComposibleTestSuite {

	public static void main(String[] args) {
		new AllTests().runAll();
    }

	protected Class[] testCases() {
		return composeTests(
				new Class[] {
						AliasesTestCase.class,
			            CallbackTestCase.class,
			            CanUpdateFalseRefreshTestCase.class,
			            CascadeDeleteDeletedTestCase.class,
			            CascadedDeleteReadTestCase.class,
			            ChangeIdentity.class,
			            CloseUnlocksFileTestCase.class,
			            ComparatorSortTestCase.class,
			            DatabaseGrowthSizeTestCase.class,
			            DatabaseUnicityTest.class,
			            DbPathDoesNotExistTestCase.class,
			            // FIXME: COR-1060
			//            DeleteSetTestCase.class,
			            DeleteReaddChildReferenceTestSuite.class,
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
			            InvalidOffsetInDeleteTestCase.class,
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
			            UnavailableClassAsTreeSetElementTestCase.class,
			            UnknownReferenceDeactivationTestCase.class,
			            UpdateDepthTestCase.class,
					});
	}
	
	/**
	 * @sharpen.if !SILVERLIGHT
	 */
	@Override
	protected Class[] composeWith() {
		return new Class[] { PersistTypeTestCase.class, };
	}
}

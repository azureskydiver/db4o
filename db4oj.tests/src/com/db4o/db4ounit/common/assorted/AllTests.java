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
            BackupStressTestCase.class,
            CanUpdateFalseRefreshTestCase.class,
            ChangeIdentity.class,
            CloseUnlocksFileTestCase.class,
            ComparatorSortTestCase.class,
            DatabaseUnicityTest.class,
            DescendToNullFieldTestCase.class,
            GetByUUIDTestCase.class,
            GetSingleSimpleArrayTestCase.class,
            IndexCreateDropTestCase.class,
            NakedObjectTestCase.class,
            ReAddCascadedDeleteTestCase.class,
            SimplestPossibleTestCase.class,
            MultiDeleteTestCase.class,
            PersistStaticFieldValuesTestCase.class,
            PersistTypeTestCase.class,
            ServerRevokeAccessTestCase.class,
            SystemInfoTestCase.class,
            ObjectVersionTest.class,
            YapClassTestCase.class,
		};
	}
}

/* Copyright (C) 2007 Versant Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.refactor;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	protected Class[] testCases() {
		return new Class[] {
			AccessOldFieldVersionsTestCase.class,
			AccessRemovedFieldTestCase.class,
			ClassRenameByConfigTestCase.class,
			ClassRenameByStoredClassTestCase.class,
			ReAddFieldTestCase.class,
			RemoveArrayFieldTestCase.class,
			RemovedClassRefactoringTestSuite.class,
		};
	}
}

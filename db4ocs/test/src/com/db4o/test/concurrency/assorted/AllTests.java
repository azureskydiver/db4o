/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.concurrency.assorted;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	protected Class[] testCases() {
		return passedTestCases();
	}

	protected Class[] passedTestCases() {
		return new Class[] { 
				DeleteUpdateIndexed.class,
				RollbackUpdate.class,
				RollbackUpdateCascade.class,
				RollbackUpdateCascadeIndexed.class, };
	}

	protected Class[] failedTestCases() {
		return new Class[] { DeleteUpdate.class, RollbackDelete.class,
				RollbackDeleteIndexedAll.class, RollbackDeleteIndexedI.class,
				RollbackDeleteIndexedS.class, RollbackUpdateIndexed.class, };
	}

	public static void main(String[] args) {
		new AllTests().runConcurrency();
	}
}

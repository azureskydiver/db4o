/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.test.concurrency.staging;

import db4ounit.extensions.*;

public class AllTests extends Db4oConcurrenyTestSuite {
	
	public static void main(String[] args) {
		new AllTests().runConcurrency();
	}

	protected Class[] testCases() {
		return new Class[] {
			CascadeDeleteArrayTestCase.class,
			CascadeToHashtableTestCase.class,
			ComparatorSortTestCase.class,
			CustomActivationDepthTestCase.class,
			HashtableTestCase.class,
			SwitchingFilesFromClientTestCase.class,
		};
	}

}

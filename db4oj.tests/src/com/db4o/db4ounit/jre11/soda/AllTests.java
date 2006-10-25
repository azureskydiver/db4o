/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre11.soda;

import com.db4o.db4ounit.jre11.soda.collections.*;

import db4ounit.extensions.*;

public class AllTests  extends Db4oTestSuite {
	protected Class[] testCases() {
		return new Class[]{
				STHashtableDTestCase.class,
				STHashtableEDTestCase.class,
				STHashtableETTestCase.class,
//				STHashtableEUTestCase.class,
//				STHashtableTTestCase.class,
//				STHashtableUTestCase.class,
		};
	}

	public static void main(String[] args) {
		new AllTests().runSolo();
	}
}

/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.fieldindex;

import db4ounit.db4o.*;

public class AllTestsFailing extends Db4oTestSuite {
	public static void main(String[] args) {
		new AllTestsFailing().runSolo();
    }

	protected Class[] testCases() {
		return new Class[] {
            FieldIndexProcessorTestCase.class,
            FailingFieldIndexTestCase.class
		};
    }

}

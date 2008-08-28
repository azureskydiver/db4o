/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.fieldindex;

import com.db4o.db4ounit.common.util.*;

import db4ounit.extensions.Db4oTestSuite;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runSolo();
    }

	protected Class[] testCases() {
		Class[] fieldBased={
				IndexedNodeTestCase.class,
	            FieldIndexTestCase.class,
	            FieldIndexProcessorTestCase.class,
	            StringFieldIndexTestCase.class,
		};
		Class[] neutral={
				DoubleFieldIndexTestCase.class,
				RuntimeFieldIndexTestCase.class,
				SecondLevelIndexTestCase.class,
	            StringIndexTestCase.class,
	            StringIndexCorruptionTestCase.class,
	            StringIndexWithSuperClassTestCase.class,
		};
		
		return Db4oUnitTestUtil.mergeClasses(neutral, fieldBased);
    }
}

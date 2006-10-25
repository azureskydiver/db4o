/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.soda;

import com.db4o.db4ounit.jre12.soda.collections.*;
import com.db4o.db4ounit.jre12.soda.deepOR.*;
import com.db4o.db4ounit.jre12.soda.experiments.*;

import db4ounit.extensions.*;

public class AllTests  extends Db4oTestSuite {
	protected Class[] testCases() {
		return new Class[]{
				STArrayListTTestCase.class,
				STArrayListUTestCase.class,
				STHashSetTTestCase.class,
				STHashSetUTestCase.class,
				STHashtableDTestCase.class,
				STHashtableEDTestCase.class,
				STHashtableETTestCase.class,
				STHashtableEUTestCase.class,
				STHashtableTTestCase.class,
				STHashtableUTestCase.class,
				STLinkedListTTestCase.class,
				STLinkedListUTestCase.class,
				STOwnCollectionTTestCase.class,
				STTreeSetTTestCase.class,
				STTreeSetUTestCase.class,
				STVectorDTestCase.class,
				STVectorEDTestCase.class,
				STVectorTTestCase.class,
				STVectorUTestCase.class,
				STOrContainsTestCase.class,
				STCurrentTestCase.class,
				STIdentityEvaluationTestCase.class,
				STNullOnPathTestCase.class,
		};
	}

	public static void main(String[] args) {
		new AllTests().runSolo();
	}
}

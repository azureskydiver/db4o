/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.nativequery;

import db4ounit.ReflectionTestSuiteBuilder;
import db4ounit.Test;
import db4ounit.TestRunner;
import db4ounit.TestSuite;
import db4ounit.extensions.Db4oTestSuiteBuilder;
import db4ounit.extensions.fixtures.Db4oSolo;

public class AllTestsNQ {
	
	public static void main(String[] args) {
		TestSuite plainTests=new ReflectionTestSuiteBuilder(
				new Class[] {
//						ExpressionBuilderTestCase.class,
//						BloatExprBuilderVisitorTestCase.class,
//						ExpressionTestCase.class,
//						BooleanReturnValueTestCase.class,
				}
		).build();
		TestSuite db4oTests=new Db4oTestSuiteBuilder(new Db4oSolo(),
					new Class[] {
						NQRegressionTestCase.class,
//						NQCatConsistencyTestCase.class,
					}
		).build();
		TestSuite allTests=new TestSuite("All native query tests",
				new Test[] {
					plainTests,
					db4oTests,
				}
		);
		System.exit(new TestRunner(allTests).run());
	}
}
